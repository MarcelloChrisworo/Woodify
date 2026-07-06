package com.woodify.view;

import com.woodify.config.SessionManager;
import com.woodify.exception.AuthenticationException;
import com.woodify.model.User;
import com.woodify.service.AuthService;
import com.woodify.service.impl.AuthServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private final AuthService authService;
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private AlertPanel alertPanel;

    // Palette Warna Mobile-Optimized
    private static final Color COLOR_SCREEN_BG = new Color(250, 246, 240); // Cream light background
    private static final Color COLOR_PRIMARY_BROWN = new Color(90, 62, 43); // Dark brown for text/logo
    private static final Color COLOR_SUBTITLE = new Color(120, 115, 110);
    private static final Color COLOR_INPUT_BG = new Color(238, 233, 225); // Soft beige matching mobile mockup
    private static final Color COLOR_INPUT_BORDER = new Color(218, 212, 204);
    private static final Color COLOR_BUTTON_GREEN = new Color(1, 74, 64); // Forest green
    private static final Color COLOR_BUTTON_GREEN_HOVER = new Color(1, 55, 46);

    public LoginFrame() {
        this.authService = new AuthServiceImpl();
        initUI();
    }

    private void initUI() {
        setTitle("Woodify - Login");
        // Ukuran viewport mobile standar (375 x 667 - aspect ratio 9:16)
        setSize(375, 667);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel Utama Mobile (Langsung mengisi seluruh frame)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(COLOR_SCREEN_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 24, 30, 24)); // Padding samping khas mobile
        setContentPane(mainPanel);

        // 1. Logo Panel (Bulat Cokelat + Sofa)
        LogoPanel logoPanel = new LogoPanel();
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(logoPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // 2. Title "WOODIFY"
        JLabel lblTitle = new JLabel("WOODIFY");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_PRIMARY_BROWN);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 4)));

        // 3. Subtitle
        JLabel lblSubtitle = new JLabel("Manajemen Retail Furnitur");
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSubtitle.setForeground(COLOR_SUBTITLE);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblSubtitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // 4. Alert Panel (Peringatan Kesalahan)
        alertPanel = new AlertPanel();
        alertPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(alertPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 5. Form Username
        JPanel txtUsernameContainer = new RoundedPanel(16, COLOR_INPUT_BG, COLOR_INPUT_BORDER);
        txtUsernameContainer.setLayout(new BorderLayout(0, 1));
        txtUsernameContainer.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        txtUsernameContainer.setMaximumSize(new Dimension(327, 52));
        txtUsernameContainer.setPreferredSize(new Dimension(327, 52));
        txtUsernameContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblUsernameTitle = new JLabel("Username");
        lblUsernameTitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblUsernameTitle.setForeground(COLOR_SUBTITLE);

        txtUsername = new JTextField();
        txtUsername.setOpaque(false);
        txtUsername.setBorder(null);
        txtUsername.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtUsername.setForeground(COLOR_PRIMARY_BROWN);
        txtUsername.setCaretColor(COLOR_PRIMARY_BROWN);

        txtUsernameContainer.add(lblUsernameTitle, BorderLayout.NORTH);
        txtUsernameContainer.add(txtUsername, BorderLayout.CENTER);
        mainPanel.add(txtUsernameContainer);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 14)));

        // 6. Form Password
        JPanel txtPasswordContainer = new RoundedPanel(16, COLOR_INPUT_BG, COLOR_INPUT_BORDER);
        txtPasswordContainer.setLayout(new BorderLayout(5, 1));
        txtPasswordContainer.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        txtPasswordContainer.setMaximumSize(new Dimension(327, 52));
        txtPasswordContainer.setPreferredSize(new Dimension(327, 52));
        txtPasswordContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPasswordTitle = new JLabel("Password");
        lblPasswordTitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblPasswordTitle.setForeground(COLOR_SUBTITLE);

        txtPassword = new JPasswordField();
        txtPassword.setOpaque(false);
        txtPassword.setBorder(null);
        txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtPassword.setForeground(COLOR_PRIMARY_BROWN);
        txtPassword.setCaretColor(COLOR_PRIMARY_BROWN);
        txtPassword.setEchoChar('•');

        EyeToggleButton btnEye = new EyeToggleButton();
        btnEye.addActionListener(e -> {
            if (btnEye.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });

        txtPasswordContainer.add(lblPasswordTitle, BorderLayout.NORTH);
        txtPasswordContainer.add(txtPassword, BorderLayout.CENTER);
        txtPasswordContainer.add(btnEye, BorderLayout.EAST);
        mainPanel.add(txtPasswordContainer);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 24)));

        // 7. Tombol Login
        btnLogin = new RoundedButton("Login", 22, COLOR_BUTTON_GREEN, COLOR_BUTTON_GREEN_HOVER);
        btnLogin.setMaximumSize(new Dimension(327, 44));
        btnLogin.setPreferredSize(new Dimension(327, 44));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Enter key listener on password field
        txtPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        mainPanel.add(btnLogin);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 8. Footer Link
        JButton btnForgotPassword = new JButton("Lupa Password?");
        btnForgotPassword.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnForgotPassword.setForeground(COLOR_PRIMARY_BROWN);
        btnForgotPassword.setContentAreaFilled(false);
        btnForgotPassword.setBorderPainted(false);
        btnForgotPassword.setFocusPainted(false);
        btnForgotPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnForgotPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnForgotPassword.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Silakan hubungi Admin atau Owner untuk mereset kata sandi Anda.", 
                "Lupa Password", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        mainPanel.add(btnForgotPassword);
    }

    private void handleLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        try {
            alertPanel.hideMessage();
            User loggedInUser = authService.authenticate(username, password);
            
            // Tutup LoginFrame, Buka MainFrame
            dispose();
            
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            });
        } catch (AuthenticationException ex) {
            alertPanel.showMessage("Username atau password salah.");
        } catch (Exception ex) {
            alertPanel.showMessage("Terjadi kesalahan sistem.");
            ex.printStackTrace();
        }
    }

    // --- CUSTOM COMPONENTS ---

    // 1. Panel Rounded untuk Container & Input
    private static class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color backgroundColor;
        private final Color borderColor;

        public RoundedPanel(int radius, Color bgColor, Color borderCol) {
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            this.borderColor = borderCol;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw Background
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

            // Draw Border
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // 2. Tombol Login Capsule Shape
    private static class RoundedButton extends JButton {
        private final int cornerRadius;
        private final Color hoverColor;
        private final Color normalColor;

        public RoundedButton(String text, int radius, Color normalColor, Color hoverColor) {
            super(text);
            this.cornerRadius = radius;
            this.normalColor = normalColor;
            this.hoverColor = hoverColor;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isRollover()) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(normalColor);
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // 3. Logo Bulat + Vector Sofa Cokelat
    private static class LogoPanel extends JPanel {
        private final Color circleBg = new Color(90, 62, 43); // Dark brown
        private final Color sofaColor = new Color(234, 218, 202); // Cream/light beige

        public LogoPanel() {
            setPreferredSize(new Dimension(64, 64));
            setMaximumSize(new Dimension(64, 64));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int r = 32; // radius

            // Draw dark brown circle
            g2.setColor(circleBg);
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);

            // Draw sofa
            g2.setColor(sofaColor);
            int sw = 28; // sofa width
            int sy = cy - 2;

            // Backrest
            g2.fillRoundRect(cx - sw/2 + 2, sy - 8, sw - 4, 10, 4, 4);
            // Armrests
            g2.fillRoundRect(cx - sw/2, sy - 4, 3, 11, 3, 3);
            g2.fillRoundRect(cx + sw/2 - 3, sy - 4, 3, 11, 3, 3);
            // Seat
            g2.fillRoundRect(cx - sw/2 + 2, sy + 1, sw - 4, 5, 2, 2);
            // Legs
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(cx - sw/2 + 4, sy + 6, cx - sw/2 + 2, sy + 9);
            g2.drawLine(cx + sw/2 - 4, sy + 6, cx + sw/2 - 2, sy + 9);

            g2.dispose();
        }
    }

    // 4. Panel Peringatan Kesalahan (Alert)
    private static class AlertPanel extends JPanel {
        private final JLabel lblMessage;

        public AlertPanel() {
            setLayout(new BorderLayout(8, 0));
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            setOpaque(false);

            // Icon exclamation
            JLabel lblIcon = new JLabel("ⓘ");
            lblIcon.setFont(new Font("SansSerif", Font.BOLD, 14));
            lblIcon.setForeground(new Color(185, 28, 28));

            lblMessage = new JLabel("Username atau password salah.");
            lblMessage.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblMessage.setForeground(new Color(153, 27, 27));

            add(lblIcon, BorderLayout.WEST);
            add(lblMessage, BorderLayout.CENTER);
            setVisible(false); // hidden by default

            // Set fixed height to prevent structural movement
            setMaximumSize(new Dimension(327, 38));
            setPreferredSize(new Dimension(327, 38));
        }

        public void showMessage(String text) {
            lblMessage.setText(text);
            setVisible(true);
            revalidate();
            repaint();
        }

        public void hideMessage() {
            setVisible(false);
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (!isVisible()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(254, 226, 226)); // Background #FEE2E2
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.setColor(new Color(254, 202, 202)); // Border #FECACA
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // 5. Tombol Toggle Show/Hide Password (Mata dengan/tanpa slash)
    private static class EyeToggleButton extends JToggleButton {
        public EyeToggleButton() {
            setPreferredSize(new Dimension(22, 22));
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(new Color(110, 105, 100));

            int w = getWidth();
            int h = getHeight();
            int cx = w / 2;
            int cy = h / 2;

            // Draw almond eye shape
            g2.drawArc(cx - 7, cy - 6, 14, 12, 0, 180);
            g2.drawArc(cx - 7, cy - 6, 14, 12, 180, 180);

            // Draw pupil
            g2.fillOval(cx - 2, cy - 2, 4, 4);

            // Draw slash if NOT selected (default hidden state shows eye-slash)
            if (!isSelected()) {
                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(new Color(110, 105, 100));
                g2.drawLine(cx - 6, cy - 4, cx + 6, cy + 4);
            }
            g2.dispose();
        }
    }
}
