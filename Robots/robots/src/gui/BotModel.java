package gui;

import java.util.Random;

public class BotModel extends RobotModel {

    public interface ShotListener {
        void onBotShot(double fromX, double fromY, double angleRad);
    }

    private static final Random random         = new Random();
    private static final int    MARGIN         = 40;
    private static final long   SHOOT_DELAY_MS = 3000; // выстрел раз в 3 секунды
    private static final double BOT_VELOCITY   = 0.04; // ← вдвое медленнее игрока (0.1)

    private int fieldWidth  = 600;
    private int fieldHeight = 500;

    private long lastShotTime = 0;

    private final RobotModel player;
    private       ShotListener shotListener;

    public BotModel(RobotModel player, double startX, double startY) {
        this.player = player;
        reset(startX, startY);
        pickRandomTarget();
    }

    /** Переопределяем скорость — бот медленнее игрока */
    @Override
    protected double getMaxVelocity() { return BOT_VELOCITY; }

    public void setFieldSize(int w, int h) {
        fieldWidth  = w;
        fieldHeight = h;
    }

    public void setShotListener(ShotListener l) {
        this.shotListener = l;
    }

    public void updateBot() {
        double dist = Math.hypot(
                getRobotPositionX() - getTargetPositionX(),
                getRobotPositionY() - getTargetPositionY()
        );
        if (dist < 12.0) pickRandomTarget();

        updateModel();

        long now = System.currentTimeMillis();
        if (now - lastShotTime >= SHOOT_DELAY_MS && shotListener != null) {
            lastShotTime = now;
            double angle = Math.atan2(
                    player.getRobotPositionY() - getRobotPositionY(),
                    player.getRobotPositionX() - getRobotPositionX()
            );
            shotListener.onBotShot(getRobotPositionX(), getRobotPositionY(), angle);
        }
    }

    public void respawn() {
        double x = MARGIN + random.nextInt(Math.max(1, fieldWidth  - MARGIN * 2));
        double y = MARGIN + random.nextInt(Math.max(1, fieldHeight - MARGIN * 2));
        reset(x, y);
        pickRandomTarget();
    }

    private void pickRandomTarget() {
        int x = MARGIN + random.nextInt(Math.max(1, fieldWidth  - MARGIN * 2));
        int y = MARGIN + random.nextInt(Math.max(1, fieldHeight - MARGIN * 2));
        setTargetPosition(x, y);
    }
}