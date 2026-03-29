// WindowStateConfig.java
package gui;

import java.awt.Rectangle;
import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class WindowStateConfig {
    private static final String CONFIG_DIR = System.getProperty("user.home") + "/.robots";
    private static final String CONFIG_FILE = CONFIG_DIR + "/window-state.properties";

    private Properties properties;

    public WindowStateConfig() {
        properties = new Properties();
        load();
    }

    public void saveWindowState(String windowId, Rectangle bounds, boolean isIcon) {
        String prefix = windowId + ".";
        properties.setProperty(prefix + "x", String.valueOf(bounds.x));
        properties.setProperty(prefix + "y", String.valueOf(bounds.y));
        properties.setProperty(prefix + "width", String.valueOf(bounds.width));
        properties.setProperty(prefix + "height", String.valueOf(bounds.height));
        properties.setProperty(prefix + "icon", String.valueOf(isIcon));
        save();
    }

    public Rectangle getWindowBounds(String windowId) {
        String prefix = windowId + ".";
        if (!properties.containsKey(prefix + "x")) {
            return null;
        }
        try {
            int x = Integer.parseInt(properties.getProperty(prefix + "x"));
            int y = Integer.parseInt(properties.getProperty(prefix + "y"));
            int width = Integer.parseInt(properties.getProperty(prefix + "width"));
            int height = Integer.parseInt(properties.getProperty(prefix + "height"));
            return new Rectangle(x, y, width, height);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean isWindowIconified(String windowId) {
        String prefix = windowId + ".";
        return Boolean.parseBoolean(properties.getProperty(prefix + "icon", "false"));
    }

    private void load() {
        Path configPath = Paths.get(CONFIG_FILE);
        if (Files.exists(configPath)) {
            try (InputStream input = new FileInputStream(CONFIG_FILE)) {
                properties.load(input);
            } catch (IOException e) {
                System.err.println("Не удалось загрузить конфигурацию: " + e.getMessage());
            }
        }
    }

    private void save() {
        try {
            Path configDir = Paths.get(CONFIG_DIR);
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }
            try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
                properties.store(output, "configuration");
            }
        } catch (IOException e) {
            System.err.println("Не удалось сохранить конфигурацию: " + e.getMessage());
        }
    }
}