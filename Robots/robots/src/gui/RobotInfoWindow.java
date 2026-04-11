package gui;

import javax.swing.*;
import java.awt.*;

public class RobotInfoWindow extends JInternalFrame implements RobotModel.RobotListener
{
    private final RobotModel model;

    private final JLabel lblX = new JLabel("X: 100.00");
    private final JLabel lblY = new JLabel("Y: 100.00");
    private final JLabel lblDirection = new JLabel("Direction: 0.0000 rad");

    public RobotInfoWindow(RobotModel model)
    {
        super("Координаты робота", true, true, true, true);
        this.model = model;
        model.addListener(this);

        initUI();
        updateInfo();
    }

    private void initUI()
    {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Font font = new Font("Monospaced", Font.BOLD, 14);
        lblX.setFont(font);
        lblY.setFont(font);
        lblDirection.setFont(font);

        panel.add(lblX);
        panel.add(lblY);
        panel.add(lblDirection);

        setContentPane(panel);
        pack();
        setSize(320, 190);
    }

    private void updateInfo()
    {
        lblX.setText(String.format("X: %.2f", model.getRobotPositionX()));
        lblY.setText(String.format("Y: %.2f", model.getRobotPositionY()));
        double dir = model.getRobotDirection();
        lblDirection.setText(String.format("Direction: %.4f rad (%.1f°)", dir, Math.toDegrees(dir)));
    }

    @Override
    public void onRobotStateChanged(RobotModel model)
    {
        EventQueue.invokeLater(this::updateInfo);
    }
}