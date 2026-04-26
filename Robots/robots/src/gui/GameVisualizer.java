package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GameVisualizer extends JPanel implements RobotModel.RobotListener {

    private final RobotModel player;
    private final BotModel   bot;

    // Обычный ArrayList — доступ только из одного таймера, синхронизируем вручную
    private final List<Bullet> bullets = new ArrayList<>();

    private volatile int scorePlayer = 0;
    private volatile int scoreBot    = 0;

    private long lastPlayerShot = 0;
    private static final long PLAYER_SHOOT_DELAY_MS = 300;

    private final Timer m_timer = new Timer("events generator", true);

    public GameVisualizer(RobotModel player) {
        this.player = player;
        player.addListener(this);

        bot = new BotModel(player, 500, 400);
        bot.addListener(this);

        bot.setShotListener((fromX, fromY, angle) -> {
            synchronized (bullets) {
                bullets.add(new Bullet(fromX, fromY, angle, Bullet.Owner.BOT));
            }
        });

        // Перерисовка
        m_timer.schedule(new TimerTask() {
            @Override public void run() {
                EventQueue.invokeLater(GameVisualizer.this::repaint);
            }
        }, 0, 50);

        // Обновление игрока
        m_timer.schedule(new TimerTask() {
            @Override public void run() {
                player.updateModel();
            }
        }, 0, 10);

        // Обновление бота + пуль (один поток — нет гонки на bullets)
        m_timer.schedule(new TimerTask() {
            @Override public void run() {
                bot.setFieldSize(getWidth(), getHeight());
                bot.updateBot();
                updateBullets();
            }
        }, 0, 10);

        // ЛКМ — задать цель
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    System.out.println("Цель: " + e.getX() + ", " + e.getY());
                    player.setTargetPosition(e.getX(), e.getY());
                }
            }
        });

        // ПРОБЕЛ — выстрел
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    long now = System.currentTimeMillis();
                    if (now - lastPlayerShot >= PLAYER_SHOOT_DELAY_MS) {
                        lastPlayerShot = now;
                        synchronized (bullets) {
                            bullets.add(new Bullet(
                                    player.getRobotPositionX(),
                                    player.getRobotPositionY(),
                                    player.getRobotDirection(),
                                    Bullet.Owner.PLAYER
                            ));
                        }
                    }
                }
            }
        });

        setDoubleBuffered(true);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    // ── Логика пуль ──────────────────────────────────────────────────────

    private void updateBullets() {
        synchronized (bullets) {
            // Собираем пули на удаление отдельно — не трогаем список во время итерации
            List<Bullet> toRemove = new ArrayList<>();

            for (Bullet b : bullets) {
                if (!b.isActive()) {
                    toRemove.add(b);
                    continue;
                }

                b.update();

                if (b.isOutOfBounds(getWidth(), getHeight())) {
                    b.deactivate();
                    toRemove.add(b);
                    continue;
                }

                if (b.getOwner() == Bullet.Owner.PLAYER && b.hits(bot)) {
                    b.deactivate();
                    toRemove.add(b);
                    scorePlayer++;
                    bot.respawn();

                } else if (b.getOwner() == Bullet.Owner.BOT && b.hits(player)) {
                    b.deactivate();
                    toRemove.add(b);
                    scoreBot++;
                }
            }

            bullets.removeAll(toRemove);
        }
    }

    @Override
    public void onRobotStateChanged(RobotModel m) {
        EventQueue.invokeLater(this::repaint);
    }

    // ── Отрисовка ────────────────────────────────────────────────────────

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawTarget(g2d, player.getTargetPositionX(), player.getTargetPositionY());

        synchronized (bullets) {
            for (Bullet b : bullets)
                if (b.isActive()) drawBullet(g2d, b);
        }

        drawRobot(g2d,
                (int) Math.round(bot.getRobotPositionX()),
                (int) Math.round(bot.getRobotPositionY()),
                bot.getRobotDirection(), Color.RED);

        drawRobot(g2d,
                (int) Math.round(player.getRobotPositionX()),
                (int) Math.round(player.getRobotPositionY()),
                player.getRobotDirection(), Color.MAGENTA);

        drawHUD(g2d);
    }

    private void drawHUD(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 130));
        g.fillRoundRect(8, 6, 230, 48, 10, 10);

        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        g.setColor(Color.MAGENTA);
        g.drawString(String.format("Вы:  %d очков", scorePlayer), 16, 26);
        g.setColor(Color.RED);
        g.drawString(String.format("Бот: %d очков", scoreBot), 16, 46);

        g.setColor(new Color(200, 200, 200));
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.drawString("ЛКМ — двигаться  |  ПРОБЕЛ — выстрел", 10, getHeight() - 8);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction, Color color) {
        AffineTransform saved = g.getTransform();
        g.transform(AffineTransform.getRotateInstance(direction, x, y));
        g.setColor(color);
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

    private void drawBullet(Graphics2D g, Bullet b) {
        Color c = b.getOwner() == Bullet.Owner.PLAYER
                ? new Color(255, 230, 50)
                : new Color(255, 100, 30);
        int bx = (int) b.getX(), by = (int) b.getY();
        g.setColor(c);
        g.fillOval(bx - 4, by - 4, 8, 8);
        g.setColor(c.darker());
        g.drawOval(bx - 4, by - 4, 8, 8);
    }

    private static void fillOval(Graphics g, int cx, int cy, int d1, int d2) {
        g.fillOval(cx - d1 / 2, cy - d2 / 2, d1, d2);
    }

    private static void drawOval(Graphics g, int cx, int cy, int d1, int d2) {
        g.drawOval(cx - d1 / 2, cy - d2 / 2, d1, d2);
    }
}