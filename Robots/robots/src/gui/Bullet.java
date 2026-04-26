package gui;

/**
 * Пуля: летит по прямой, проверяет попадание по радиусу.
 * Owner — чья пуля, чтобы не попасть в своего.
 */
public class Bullet {

    public enum Owner { PLAYER, BOT }

    private double x, y;
    private final double dx, dy;
    private final Owner owner;
    private boolean active = true;

    private static final double SPEED      = 5.0;
    private static final double HIT_RADIUS = 15.0;

    public Bullet(double startX, double startY, double angleRad, Owner owner) {
        this.x     = startX;
        this.y     = startY;
        this.dx    = Math.cos(angleRad) * SPEED;
        this.dy    = Math.sin(angleRad) * SPEED;
        this.owner = owner;
    }

    /** Сдвинуть пулю на один шаг */
    public void update() {
        x += dx;
        y += dy;
    }

    /** Попала ли пуля в робота (по расстоянию до центра) */
    public boolean hits(RobotModel robot) {
        double ddx = x - robot.getRobotPositionX();
        double ddy = y - robot.getRobotPositionY();
        return Math.sqrt(ddx * ddx + ddy * ddy) < HIT_RADIUS;
    }

    /** Вышла ли пуля за пределы поля */
    public boolean isOutOfBounds(int w, int h) {
        return x < -10 || y < -10 || x > w + 10 || y > h + 10;
    }

    public double getX()        { return x; }
    public double getY()        { return y; }
    public Owner  getOwner()    { return owner; }
    public boolean isActive()   { return active; }
    public void   deactivate()  { active = false; }
}