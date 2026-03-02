package gui;

import state.AppStateManager;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;

import javax.swing.*;

import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final AppStateManager appStateManager = new AppStateManager();

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

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        appStateManager.register("logger.", logWindow);
        appStateManager.register("game.", gameWindow);
        appStateManager.loadAll();

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (confirmExit()) {
                    appStateManager.saveAll();
                    System.exit(0);
                }
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

//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
// 
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
// 
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");

    /// /        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        return menuBar;
//    }
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

    private JMenuItem createExitMenuItem() {
        JMenuItem item = createMenuItem("Выход", KeyEvent.VK_X,
                event -> {
                    if (confirmExit()) {
                        appStateManager.saveAll();
                        System.exit(0);
                    }
                });
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
}
