package Utils;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class IconHelper {

    public static final int DEFAULT_SIZE = 16;

    public static ImageIcon getIcon(String path, int width, int height) {
        URL url = IconHelper.class.getResource(path);
        if (url == null) {
            System.out.println("Icon tidak ditemukan: " + path);
            return null;
        }

        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return new ImageIcon(img);
    }

    public static ImageIcon getIcon(String path) {
        return getIcon(path, DEFAULT_SIZE, DEFAULT_SIZE);
    }

    public static void setIcon(JLabel label, String path) {
        label.setIcon(getIcon(path));
    }

    public static void setIcon(JButton button, String path) {
        button.setIcon(getIcon(path));
    }
}