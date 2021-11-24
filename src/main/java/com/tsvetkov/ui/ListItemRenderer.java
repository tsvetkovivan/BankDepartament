package com.tsvetkov.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ListItemRenderer extends DefaultListCellRenderer {
    private ImageIcon ico;
    private static Font font;

    ListItemRenderer (String icoName) {
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/" + icoName));
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(100, 100,  java.awt.Image.SCALE_SMOOTH);
        ico = new ImageIcon(newimg);
        font = new Font("helvitica", Font.BOLD, 24);
    }

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

        label.setIcon(ico);
        label.setFont(font);
        //label.setText("      " + ((TEST)value).getText1());
        label.setHorizontalTextPosition(JLabel.RIGHT);
        setBorder(new EmptyBorder(0, 0, 10, 0));
        return label;
    }
}