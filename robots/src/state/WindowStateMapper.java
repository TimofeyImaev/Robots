package state;

import java.awt.Component;
import java.awt.Frame;
import java.beans.PropertyVetoException;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.JFrame;

final class WindowStateMapper {
    private WindowStateMapper() {
    }

    static void saveWindowState(Map<String, String> state, String prefix, Component window) {
        state.put(prefix + "x", Integer.toString(window.getX()));
        state.put(prefix + "y", Integer.toString(window.getY()));
        state.put(prefix + "width", Integer.toString(window.getWidth()));
        state.put(prefix + "height", Integer.toString(window.getHeight()));
        state.put(prefix + "visible", Boolean.toString(window.isVisible()));

        if (window instanceof JFrame frame) {
            int extendedState = frame.getExtendedState();
            state.put(prefix + "extendedState", Integer.toString(extendedState));
            state.put(prefix + "iconified", Boolean.toString((extendedState & Frame.ICONIFIED) != 0));
            return;
        }
        if (window instanceof JInternalFrame frame) {
            state.put(prefix + "iconified", Boolean.toString(frame.isIcon()));
        }
    }

    static void restoreWindowState(Map<String, String> state, String prefix, Component window) {
        String x = state.get(prefix + "x");
        String y = state.get(prefix + "y");
        String width = state.get(prefix + "width");
        String height = state.get(prefix + "height");
        String visible = state.get(prefix + "visible");
        String iconified = state.get(prefix + "iconified");
        String extendedState = state.get(prefix + "extendedState");

        restoreBounds(window, x, y, width, height);
        restoreVisible(window, visible);
        restoreFrameSpecificState(window, extendedState, iconified);
    }

    private static void restoreBounds(Component window, String x, String y, String width, String height) {
        try {
            int ix = Integer.parseInt(x);
            int iy = Integer.parseInt(y);
            int iw = Integer.parseInt(width);
            int ih = Integer.parseInt(height);
            if (iw > 0 && ih > 0) {
                window.setBounds(ix, iy, iw, ih);
            }
        } catch (Exception ignored) {
        }
    }

    private static void restoreVisible(Component window, String visible) {
        if (visible != null) {
            window.setVisible(Boolean.parseBoolean(visible));
        }
    }

    private static void restoreFrameSpecificState(Component window, String extendedState, String iconified) {
        if (window instanceof JFrame frame) {
            restoreJFrameState(frame, extendedState, iconified);
            return;
        }

        if (window instanceof JInternalFrame frame && iconified != null) {
            try {
                frame.setIcon(Boolean.parseBoolean(iconified));
            } catch (PropertyVetoException ignored) {
            }
        }
    }

    private static void restoreJFrameState(JFrame frame, String extendedState, String iconified) {
        if (extendedState != null) {
            try {
                frame.setExtendedState(Integer.parseInt(extendedState));
            } catch (Exception ignored) {
            }
        }
        if (iconified != null) {
            boolean isIconified = Boolean.parseBoolean(iconified);
            int current = frame.getExtendedState();
            if (isIconified) {
                frame.setExtendedState(current | Frame.ICONIFIED);
            } else {
                frame.setExtendedState(current & ~Frame.ICONIFIED);
            }
        }
    }
}

