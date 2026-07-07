package com.woodify.view;

import com.woodify.config.SessionManager;
import com.woodify.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ProfilPanel extends BasePanel {

    private JLabel lblAvatar;
    private JLabel lblName;
    private JLabel lblRole;
    private final Runnable logoutAction;

    private static final Color COLOR_BG = new Color(255, 248, 245);
    private static final Color COLOR_TEXT_DARK = new Color(74, 35, 17);
    private static final Color COLOR_TEXT_MUTED = new Color(130, 100, 90);
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_DANGER_RED = new Color(200, 35, 50);

    public ProfilPanel(Runnable logoutAction) {
        super("Profil");
        this.logoutAction = logoutAction;
        initUI();
    }

    private void initUI() {
        removeAll();
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(COLOR_BG);
        contentPanel.setBorder(new EmptyBorder(40, 24, 40, 24));

        // 1. Avatar (Large circle with letter)
        JPanel avatarWrapper = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(90, 60, 50));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        avatarWrapper.setOpaque(false);
        avatarWrapper.setPreferredSize(new Dimension(90, 90));
        avatarWrapper.setMaximumSize(new Dimension(90, 90));
        avatarWrapper.setLayout(new BorderLayout());
        avatarWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblAvatar = new JLabel("👤", SwingConstants.CENTER);
        lblAvatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 46));
        lblAvatar.setForeground(Color.WHITE);
        avatarWrapper.add(lblAvatar, BorderLayout.CENTER);
        contentPanel.add(avatarWrapper);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. User Info Card
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(new Color(225, 218, 214));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setMaximumSize(new Dimension(340, 160));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNameTitle = new JLabel("Nama Lengkap");
        lblNameTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblNameTitle.setForeground(COLOR_TEXT_MUTED);

        lblName = new JLabel("Nama User");
        lblName.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblName.setForeground(COLOR_TEXT_DARK);

        JLabel lblRoleTitle = new JLabel("Peran / Hak Akses");
        lblRoleTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblRoleTitle.setForeground(COLOR_TEXT_MUTED);

        lblRole = new JLabel("Role");
        lblRole.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblRole.setForeground(COLOR_PRIMARY);

        card.add(lblNameTitle);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(lblName);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(lblRoleTitle);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(lblRole);
        contentPanel.add(card);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // 3. Logout Button (Rounded capsule)
        JButton btnLogout = new JButton("Keluar / Logout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_DANGER_RED);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 22, 22));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogout.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(340, 45));
        btnLogout.setPreferredSize(new Dimension(340, 45));
        btnLogout.addActionListener(e -> {
            if (logoutAction != null) logoutAction.run();
        });
        contentPanel.add(btnLogout);

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.setBackground(COLOR_BG);
        add(scroll, BorderLayout.CENTER);
    }

    @Override
    public void onPageLoad() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            lblName.setText(user.getNamaLengkap());
            lblRole.setText(user.getRoleDisplay());
            
            // Set first letter as avatar if no symbol
            String firstLetter = user.getNamaLengkap().isEmpty() ? "👤" : String.valueOf(user.getNamaLengkap().charAt(0)).toUpperCase();
            // Let's use first letter of name as text for avatar panel, or just stick to 👤 icon
            // Let's stick to 👤 for classic look or the letter
            lblAvatar.setText(firstLetter.matches("[A-Z]") ? firstLetter : "👤");
        }
    }
}
