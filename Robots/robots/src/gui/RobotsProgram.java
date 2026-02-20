package gui;

import java.awt.Frame;
import java.util.Locale;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.omegat.swing.extra.ExtraLocales;
public class RobotsProgram
{
    public static void main(String[] args) {
        Locale.setDefault(new Locale("ru", "RU"));


//        try {
//            ExtraLocales.initialize();
//         } catch (Exception e) {
//             e.printStackTrace();
//         }


        UIManager.put("OptionPane.yesButtonText", "ДА");
        UIManager.put("OptionPane.noButtonText", "НЕТ");
        UIManager.put("OptionPane.okButtonText", "ОКЕЙ");
        UIManager.put("OptionPane.cancelButtonText", "ОТМЕНА");

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
