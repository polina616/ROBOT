package gui;

import java.awt.Frame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class RobotsProgram
{
    //cd C:\Users\sofam\IdeaProjects\ROBOT\Robots\robots
    //mvn compile exec:java
    public static void main(String[] args) {

        RussianLocalizer.setup();

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainApplicationFrame frame = new MainApplicationFrame();
            frame.pack();
            frame.setVisible(true);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        });
    }
    }
