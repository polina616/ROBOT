package gui;

import java.util.ArrayList;
import java.util.List;

public class RobotModel
{
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private static final double MAX_VELOCITY         = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.001;
    private static final double ANGLE_THRESHOLD      = 0.005;

    // Радиус остановки увеличен: робот тормозит раньше чем проскочит
    private static final double STOP_RADIUS    = 10.0;
    // На этом расстоянии начинаем плавно снижать скорость
    private static final double SLOWDOWN_RADIUS = 40.0;

    private final List<RobotListener> listeners = new ArrayList<>();

    public interface RobotListener {
        void onRobotStateChanged(RobotModel model);
    }

    public void addListener(RobotListener listener) {
        synchronized (listeners) { listeners.add(listener); }
    }

    public void removeListener(RobotListener listener) {
        synchronized (listeners) { listeners.remove(listener); }
    }

    protected void notifyListeners() {
        RobotListener[] array;
        synchronized (listeners) { array = listeners.toArray(new RobotListener[0]); }
        for (RobotListener l : array) l.onRobotStateChanged(this);
    }

    public double getRobotPositionX() { return m_robotPositionX; }
    public double getRobotPositionY() { return m_robotPositionY; }
    public double getRobotDirection()  { return m_robotDirection; }
    public int    getTargetPositionX() { return m_targetPositionX; }
    public int    getTargetPositionY() { return m_targetPositionY; }

    public void reset(double x, double y) {
        m_robotPositionX  = x;
        m_robotPositionY  = y;
        m_robotDirection  = 0;
        m_targetPositionX = (int) x;
        m_targetPositionY = (int) y;
        notifyListeners();
    }

    public void setTargetPosition(int x, int y) {
        m_targetPositionX = x;
        m_targetPositionY = y;
        notifyListeners();
    }

    public void updateModel() {
        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX,  m_robotPositionY);

        // Стоим если уже близко
        if (distance < STOP_RADIUS) return;

        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY,
                m_targetPositionX, m_targetPositionY);

        double angleDiff = angleToTarget - m_robotDirection;
        while (angleDiff >  Math.PI) angleDiff -= 2 * Math.PI;
        while (angleDiff < -Math.PI) angleDiff += 2 * Math.PI;

        double angularVelocity = 0;
        if (Math.abs(angleDiff) > ANGLE_THRESHOLD) {
            angularVelocity = angleDiff > 0 ? MAX_ANGULAR_VELOCITY : -MAX_ANGULAR_VELOCITY;
        }

        // Скорость: сначала замедляемся по углу, потом ещё по расстоянию
        double alignFactor    = Math.max(0, Math.cos(angleDiff));
        double distanceFactor = Math.min(1.0, distance / SLOWDOWN_RADIUS);
        double velocity       = MAX_VELOCITY * alignFactor * distanceFactor;

        moveRobot(velocity, angularVelocity, 10);
        notifyListeners();
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2, dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    static double angleTo(double fromX, double fromY, double toX, double toY) {
        return asNormalizedRadians(Math.atan2(toY - fromY, toX - fromX));
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity        = applyLimits(velocity,        0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        double newX, newY;
        if (Math.abs(angularVelocity) < 1e-10) {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        } else {
            newX = m_robotPositionX + velocity / angularVelocity *
                    (Math.sin(m_robotDirection + angularVelocity * duration) - Math.sin(m_robotDirection));
            newY = m_robotPositionY - velocity / angularVelocity *
                    (Math.cos(m_robotDirection + angularVelocity * duration) - Math.cos(m_robotDirection));
            if (!Double.isFinite(newX)) newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
            if (!Double.isFinite(newY)) newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }

        m_robotPositionX = newX;
        m_robotPositionY = newY;
        m_robotDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
    }

    private static double applyLimits(double v, double min, double max) {
        return v < min ? min : v > max ? max : v;
    }

    static double asNormalizedRadians(double angle) {
        while (angle <  0)           angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }
}