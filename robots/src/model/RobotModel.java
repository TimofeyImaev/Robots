package model;

import java.util.Observable;

@SuppressWarnings("deprecation")
public class RobotModel extends Observable {
    private double robotPositionX = 100;
    private double robotPositionY = 100;
    private double robotDirection = 0;
    private int targetPositionX = 150;
    private int targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;

    public synchronized double getRobotPositionX() {
        return robotPositionX;
    }

    public synchronized double getRobotPositionY() {
        return robotPositionY;
    }

    public void setTargetPosition(int x, int y) {
        synchronized (this) {
            targetPositionX = x;
            targetPositionY = y;
        }
        setChanged();
        notifyObservers();
    }

    public void step(double duration) {
        synchronized (this) {
            double dist = distance(targetPositionX, targetPositionY, robotPositionX, robotPositionY);
            if (dist < 0.5) {
                return;
            }
            double velocity = maxVelocity;
            double angleToTarget = angleTo(robotPositionX, robotPositionY, targetPositionX, targetPositionY);
            double angularVelocity = computeShortestTurnAngularVelocity(angleToTarget, robotDirection);
            moveRobot(velocity, angularVelocity, duration);
        }
        setChanged();
        notifyObservers();
    }

    private static double computeShortestTurnAngularVelocity(double angleToTarget, double robotDirection) {
        double delta = angleToTarget - robotDirection;
        while (delta > Math.PI) {
            delta -= 2 * Math.PI;
        }
        while (delta < -Math.PI) {
            delta += 2 * Math.PI;
        }
        if (delta > 0) {
            return maxAngularVelocity;
        }
        if (delta < 0) {
            return -maxAngularVelocity;
        }
        return 0;
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
        double newX = robotPositionX + velocity / angularVelocity *
                (Math.sin(robotDirection + angularVelocity * duration) - Math.sin(robotDirection));
        if (!Double.isFinite(newX)) {
            newX = robotPositionX + velocity * duration * Math.cos(robotDirection);
        }
        double newY = robotPositionY - velocity / angularVelocity *
                (Math.cos(robotDirection + angularVelocity * duration) - Math.cos(robotDirection));
        if (!Double.isFinite(newY)) {
            newY = robotPositionY + velocity * duration * Math.sin(robotDirection);
        }
        robotPositionX = newX;
        robotPositionY = newY;
        robotDirection = asNormalizedRadians(robotDirection + angularVelocity * duration);
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }
}
