package com.woodify.view;

import javax.swing.*;
import java.awt.*;

public abstract class BasePanel extends JPanel {
    // Palet warna modern untuk Woodify
    protected static final Color COLOR_PRIMARY = new Color(27, 77, 62);    // Dark Slate Green (#1B4D3E)
    protected static final Color COLOR_SECONDARY = new Color(212, 163, 115); // Warm Amber (#D4A373)
    protected static final Color COLOR_BG_LIGHT = new Color(248, 249, 250); // Light Gray (#F8F9FA)
    protected static final Color COLOR_TEXT_DARK = new Color(33, 37, 41);    // Dark Gray (#212529)
    protected static final Color COLOR_TEXT_LIGHT = Color.WHITE;
    protected static final Color COLOR_WHITE = Color.WHITE;
    protected static final Color COLOR_DANGER = new Color(220, 53, 69);     // Soft Red (#DC3545)
    protected static final Color COLOR_SUCCESS = new Color(40, 167, 69);    // Soft Green (#28A745)

    protected static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 22);
    protected static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 14);
    protected static final Font FONT_REGULAR = new Font("SansSerif", Font.PLAIN, 12);
    protected static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 12);

    public BasePanel(String title) {
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header Halaman
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BG_LIGHT);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COLOR_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
    }

    // Method abstract untuk reload data dari database setiap kali halaman aktif dibuka
    public abstract void onPageLoad();

    // Helper untuk membuat button kustom bergaya modern
    protected JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    protected JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(FONT_REGULAR);
        field.setForeground(COLOR_TEXT_DARK);
        field.setCaretColor(COLOR_TEXT_DARK);
        field.setBackground(COLOR_WHITE);
        return field;
    }

    protected JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(FONT_REGULAR);
        area.setForeground(COLOR_TEXT_DARK);
        area.setCaretColor(COLOR_TEXT_DARK);
        area.setBackground(COLOR_WHITE);
        return area;
    }
}
