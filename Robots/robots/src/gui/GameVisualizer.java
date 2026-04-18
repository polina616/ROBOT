package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class GameVisualizer extends JPanel implements RobotModel.RobotListener
{
    private final RobotModel model; // игрок
    private final BotModel   bot;  // бот

    private final Timer m_timer = new Timer("events generator", true);

    public GameVisualizer(RobotModel model)
    {
        this.model = model;
        model.addListener(this);

        bot = new BotModel(500, 400);
        bot.addListener(this);

        // Перерисовка
        m_timer.schedule(new TimerTask() {
            @Override public void run() { onRedrawEvent(); }
        }, 0, 50);

        // Обновление игрока
        m_timer.schedule(new TimerTask() {
            @Override public void run() { model.updateModel(); }
        }, 0, 10);

        // Обновление бота
        m_timer.schedule(new TimerTask() {
            @Override public void run() {
                bot.setFieldSize(getWidth(), getHeight());
                bot.updateBot();
            }
        }, 0, 10);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model.setTargetPosition(e.getPoint().x, e.getPoint().y);
                repaint();
            }
        });

        setDoubleBuffered(true);
    }

    @Override
    public void onRobotStateChanged(RobotModel m) {
        EventQueue.invokeLater(this::repaint);
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        drawTarget(g2d, model.getTargetPositionX(), model.getTargetPositionY());

        // Бот — красный
        drawRobot(g2d,
                (int) Math.round(bot.getRobotPositionX()),
                (int) Math.round(bot.getRobotPositionY()),
                bot.getRobotDirection(),
                Color.RED);

        // Игрок — пурпурный
        drawRobot(g2d,
                (int) Math.round(model.getRobotPositionX()),
                (int) Math.round(model.getRobotPositionY()),
                model.getRobotDirection(),
                Color.MAGENTA);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction, Color bodyColor) {
        AffineTransform saved = g.getTransform();
        g.transform(AffineTransform.getRotateInstance(direction, x, y));

        g.setColor(bodyColor);
        fillOval(g, x, y, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, x + 10, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x + 10, y, 5, 5);

        g.setTransform(saved);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    private static void fillOval(Graphics g, int cx, int cy, int d1, int d2) {
        g.fillOval(cx - d1 / 2, cy - d2 / 2, d1, d2);
    }

    private static void drawOval(Graphics g, int cx, int cy, int d1, int d2) {
        g.drawOval(cx - d1 / 2, cy - d2 / 2, d1, d2);
    }
}