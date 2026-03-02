package gui;

import state.StatefulComponent;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import log.LogChangeListener;
import log.LogEntry;
import log.Logger;
import log.LogWindowSource;

public class LogWindow extends JInternalFrame implements LogChangeListener, StatefulComponent
{
    private LogWindowSource m_logSource;
    private TextArea m_logContent;

    public LogWindow(LogWindowSource logSource)
    {
        super("Протокол работы", true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    private void updateLogContent()
    {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all())
        {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    @Override
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
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
            Logger.error("Ошибка при восстановлении состояния лога " + e.getMessage());
        }

        if (visible != null) {
            setVisible(Boolean.parseBoolean(visible));
        }
    }
}
