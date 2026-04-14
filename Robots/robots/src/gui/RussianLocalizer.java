package gui;
import javax.swing.UIManager;
import java.util.Locale;
public class RussianLocalizer {
    private RussianLocalizer() {

    }

    /**
     * Устанавливает русские подписи для кнопок JOptionPane и заголовков InternalFrame.
     */
    public static void setup() {

        Locale.setDefault(new Locale("ru", "RU"));
        UIManager.put("OptionPane.yesButtonText", "ДА");
        UIManager.put("OptionPane.noButtonText", "НЕТ");
        UIManager.put("OptionPane.okButtonText", "ОКЕЙ");
        UIManager.put("OptionPane.cancelButtonText", "ОТМЕНА");


        UIManager.put("InternalFrameTitlePane.restoreButtonText", "Восстановить");
        UIManager.put("InternalFrameTitlePane.moveButtonText", "Переместить");
        UIManager.put("InternalFrameTitlePane.sizeButtonText", "Размер");
        UIManager.put("InternalFrameTitlePane.minimizeButtonText", "Свернуть");
        UIManager.put("InternalFrameTitlePane.maximizeButtonText", "Развернуть");
        UIManager.put("InternalFrameTitlePane.closeButtonText", "Закрыть");

}
}
