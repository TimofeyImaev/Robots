package gui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import model.RobotModel;

public class RobotController {
    private static final double STEP_DURATION = 10;

    private final RobotModel model;
    private final Timer timer = new Timer("robot simulation", true);

    public RobotController(RobotModel model) {
        this.model = model;
    }

    public void start() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                model.step(STEP_DURATION);
            }
        }, 0, 10);
    }

    public void attachTo(GameVisualizer view) {
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setTargetPosition(e.getPoint());
            }
        });
    }

    public void setTargetPosition(Point p) {
        model.setTargetPosition(p.x, p.y);
    }
}
