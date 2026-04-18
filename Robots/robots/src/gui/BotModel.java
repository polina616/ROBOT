package gui;

import java.util.Random;

/**
 * Робот-бот: хаотично бродит по полю.
 * Выбирает случайную точку, едет к ней, потом выбирает новую.
 */
public class BotModel extends RobotModel
{
    private static final Random random = new Random();

    // Отступ от края поля чтобы бот не застревал у стен
    private static final int MARGIN = 40;

    private int fieldWidth  = 600;
    private int fieldHeight = 500;

    public BotModel(double startX, double startY) {
        reset(startX, startY);
        pickRandomTarget();
    }

    public void setFieldSize(int width, int height) {
        fieldWidth  = width;
        fieldHeight = height;
    }

    /**
     * Вызывать каждый кадр из GameVisualizer.
     * Если бот добрался до цели — выбирает новую случайную точку.
     */
    public void updateBot() {
        double dist = Math.hypot(
                getRobotPositionX() - getTargetPositionX(),
                getRobotPositionY() - getTargetPositionY()
        );

        if (dist < 12.0) {
            pickRandomTarget();
        }

        updateModel();
    }

    private void pickRandomTarget() {
        int x = MARGIN + random.nextInt(Math.max(1, fieldWidth  - MARGIN * 2));
        int y = MARGIN + random.nextInt(Math.max(1, fieldHeight - MARGIN * 2));
        setTargetPosition(x, y);
    }
}