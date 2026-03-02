package gui;

import state.StatefulComponent;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import log.Logger;

public class GameWindow extends JInternalFrame implements StatefulComponent {
    private final GameVisualizer m_visualizer;

    public GameWindow() {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public void saveState(Map<String, String> state) {
        state.put("x", Integer.toString(getX()));
        state.put("y", Integer.toString(getY()));
        state.put("width", Integer.toString(getWidth()));
        state.put("height", Integer.toString(getHeight()));
        state.put("visible", Boolean.toString(isVisible()));
    }

    @Override
    public void restoreState(Map<String, String> state) {
        String x = state.get("x");
        String y = state.get("y");
        String width = state.get("width");
        String height = state.get("height");
        String visible = state.get("visible");

        try {
            int ix = Integer.parseInt(x);
            int iy = Integer.parseInt(y);
            int iw = Integer.parseInt(width);
            int ih = Integer.parseInt(height);
            if (iw > 0 && ih > 0) {
                setBounds(ix, iy, iw, ih);
            }
        } catch (Exception e) {
            Logger.error("Ошибка при восстановлении состояния " + e.getMessage());
        }

        if (visible != null) {
            setVisible(Boolean.parseBoolean(visible));
        }
    }
}
