package gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import log.Logger;

public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final WindowStateConfig config;

    private LogWindow logWindow;
    private GameWindow gameWindow;

    // === Новые поля для модели и информационного окна ===
    private RobotModel robotModel;
    private RobotInfoWindow robotInfoWindow;

    public MainApplicationFrame() {
        config = new WindowStateConfig();

        //Make the big window be indented 50 pixels from each edge of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);

        logWindow = createLogWindow();
        addWindow(logWindow);
        restoreWindowState("LogWindow", logWindow, new Rectangle(10,10,300,800));

        // === Инициализация модели робота и новых окон ===
        robotModel = new RobotModel();

        gameWindow = new GameWindow(robotModel);
        addWindow(gameWindow);
        restoreWindowState("GameWindow", gameWindow, new Rectangle(320,10,400,400));

        // Новое окно с координатами робота
        robotInfoWindow = new RobotInfoWindow(robotModel);
        addWindow(robotInfoWindow);
        restoreWindowState("RobotInfoWindow", robotInfoWindow, new Rectangle(730,10,320,190));

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
        logWindow.pack();
        setMinimumSize(logWindow.getSize());
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private void restoreWindowState(String windowId, JInternalFrame frame, Rectangle defaultBounds) {
        Rectangle bounds = config.getWindowBounds(windowId);
        if (bounds != null) {
            frame.setBounds(bounds);
        } else {
            frame.setBounds(defaultBounds); // дефолт, если нет сохранённого состояния
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
        saveWindowState("RobotInfoWindow", robotInfoWindow);   // ← добавлено
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