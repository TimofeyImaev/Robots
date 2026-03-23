package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Locale;

import javax.swing.*;

import log.Logger;
import state.AppStateManager;
import state.StatefulComponent;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame implements StatefulComponent {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final AppStateManager stateManager = new AppStateManager();
    private final LogWindow logWindow;
    private final GameWindow gameWindow;

    public MainApplicationFrame() {
        JOptionPane.setDefaultLocale(new Locale("ru", "RU"));

        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");

        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        logWindow = createLogWindow();
        addWindow(logWindow);

        gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        stateManager.register("main.", this);
        stateManager.register("log.", logWindow);
        stateManager.register("game.", gameWindow);
        stateManager.loadAll();

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                handleExit();
            }
        });
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuItem createMenuItem(String text, int mnemonic, ActionListener listener) {
        JMenuItem item = new JMenuItem(text, mnemonic);
        item.addActionListener(listener);
        return item;
    }

    private JMenu createLookAndFeelMenu() {
        JMenu menu = new JMenu("Режим отображения");
        menu.setMnemonic(KeyEvent.VK_V);
        menu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        menu.add(createMenuItem("Системная схема", KeyEvent.VK_S,
                event -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName())));

        menu.add(createMenuItem("Универсальная схема", KeyEvent.VK_K,
                event -> setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName())));

        return menu;
    }

    private boolean confirmExit() {
        return JOptionPane.showConfirmDialog(this,
                "Вы действительно хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void handleExit() {
        if (!confirmExit()) {
            return;
        }
        try {
            stateManager.saveAll();
        } catch (Exception e) {
            Logger.debug("Ошибка при сохранении состояния окон " + e.getMessage());
        }
        System.exit(0);
    }

    private JMenuItem createExitMenuItem() {
        JMenuItem item = createMenuItem("Выход", KeyEvent.VK_X,
                event -> handleExit());
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        return item;
    }

    private JMenu createFileMenu() {
        JMenu menu = new JMenu("Файл");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("Операции с файлами и выход");

        menu.add(createExitMenuItem());
        return menu;
    }

    private JMenu createTestMenu() {
        JMenu menu = new JMenu("Тесты");
        menu.setMnemonic(KeyEvent.VK_T);
        menu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        menu.add(createMenuItem("Сообщение в лог", KeyEvent.VK_S,
                event -> Logger.debug("Новая строка")));
        return menu;
    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());

        return menuBar;
    }

    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);

            UIManager.put("OptionPane.yesButtonText", "Да");
            UIManager.put("OptionPane.noButtonText", "Нет");

            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            Logger.debug("ошибка при смене LookAndFeel " + e.getMessage());

            JOptionPane.showMessageDialog(this,
                    "Не удалось изменить оформление  " + className,
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void saveState(Map<String, String> state) {
        state.put("x", Integer.toString(getX()));
        state.put("y", Integer.toString(getY()));
        state.put("width", Integer.toString(getWidth()));
        state.put("height", Integer.toString(getHeight()));
        state.put("visible", Boolean.toString(isVisible()));

        state.put("extendedState", Integer.toString(getExtendedState()));
    }

    @Override
    public void restoreState(Map<String, String> state) {
        String extendedState = state.get("extendedState");
        if(extendedState != null) {
            setExtendedState(Integer.parseInt(extendedState));
        }
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
