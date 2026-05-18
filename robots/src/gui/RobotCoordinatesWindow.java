package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import model.RobotModel;

@SuppressWarnings("deprecation")
public class RobotCoordinatesWindow extends JInternalFrame implements Observer {
    private final RobotModel model;
    private final JTextField coordsField;

    public RobotCoordinatesWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);
        this.model = model;
        this.model.addObserver(this);

        JLabel title = new JLabel("Позиция:");
        coordsField = new JTextField();
        coordsField.setEditable(false);

        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.add(title, BorderLayout.NORTH);
        panel.add(coordsField, BorderLayout.CENTER);
        getContentPane().add(panel);

        setSize(280, 100);
        refreshText();
    }

    private void refreshText() {
        double x = model.getRobotPositionX();
        double y = model.getRobotPositionY();
        coordsField.setText(String.format("x = %.2f,  y = %.2f", x, y));
    }

    @Override
    public void update(Observable o, Object arg) {
        EventQueue.invokeLater(this::refreshText);
    }
}
