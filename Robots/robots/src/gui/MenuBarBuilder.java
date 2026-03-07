package gui;
import javax.swing.*;
import java.awt.event.KeyEvent;
import log.Logger;
public class MenuBarBuilder {
    private final MainApplicationFrame MainApplicationFrame; // предполагаемое имя класса главного окна
    private final JMenuBar menuBar;

    public MenuBarBuilder(MainApplicationFrame MainApplicationFrame) {
        this.MainApplicationFrame = MainApplicationFrame;
        this.menuBar = new JMenuBar();
    }

    public JMenuBar build() {
        menuBar.add(createFileMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.setMnemonic(KeyEvent.VK_X);
        exitItem.addActionListener(e -> MainApplicationFrame.exitApplication());
        fileMenu.add(exitItem);

        return fileMenu;
    }

    private JMenu createLookAndFeelMenu() {
        JMenu lafMenu = new JMenu("Режим отображения");
        lafMenu.setMnemonic(KeyEvent.VK_V);
        lafMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        JMenuItem systemLaf = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLaf.addActionListener(e -> {
            MainApplicationFrame.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            MainApplicationFrame.invalidate();
        });
        lafMenu.add(systemLaf);

        JMenuItem crossLaf = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossLaf.addActionListener(e -> {
            MainApplicationFrame.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            MainApplicationFrame.invalidate();
        });
        lafMenu.add(crossLaf);

        return lafMenu;
    }

    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        JMenuItem addLogItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogItem.addActionListener(e -> Logger.debug("Новая строка"));
        testMenu.add(addLogItem);

        return testMenu;
    }
}
