package gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import log.Logger;
import javax.swing.JOptionPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowStateConfig config;
    private LogWindow logWindow;
    private GameWindow gameWindow;

    public MainApplicationFrame() {
        config = new WindowStateConfig();

        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);

        logWindow = createLogWindow();
        addWindow(logWindow);
        restoreWindowState("LogWindow", logWindow);

        gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);
        restoreWindowState("GameWindow", gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private void restoreWindowState(String windowId, JInternalFrame frame) {
        Rectangle bounds = config.getWindowBounds(windowId);
        if (bounds != null) {
            frame.setBounds(bounds);
        }

        boolean isIconified = config.isWindowIconified(windowId);
        if (isIconified) {
            try {
                frame.setIcon(true);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private void saveAllWindowsState() {
        saveWindowState("LogWindow", logWindow);
        saveWindowState("GameWindow", gameWindow);
    }

    private void saveWindowState(String windowId, JInternalFrame frame) {
        if (frame != null) {
            Rectangle bounds = frame.getBounds();
            boolean isIconified = frame.isIcon();
            config.saveWindowState(windowId, bounds, isIconified);
        }
    }

    private JMenuBar generateMenuBar()
    {
        return new MenuBarBuilder(this).build();
    }

    void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }

    void exitApplication() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "вы действительно хотите выйти?",
                "подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            saveAllWindowsState();
            System.exit(0);
        }
    }
}