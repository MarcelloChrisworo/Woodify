package com.woodify.view;

import com.woodify.config.SessionManager;
import com.woodify.model.User;
import com.woodify.view.dashboard.DashboardPanel;
import com.woodify.view.product.ProdukPanel;
import com.woodify.view.customer.PelangganPanel;
import com.woodify.view.transaction.TransaksiPanel;
import com.woodify.view.report.LaporanPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final JPanel bottomNavPanel;
    private final User currentUser;

    private final Map<String, BasePanel> panels = new HashMap<>();
    private final Map<String, NavButton> navButtons = new HashMap<>();

    // Palette warna mobile
    private static final Color COLOR_PRIMARY = new Color(27, 77, 62);    // Forest green
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_TOPBAR_BG = new Color(255, 248, 245); // Cream background
    private static final Color COLOR_NAV_BG = new Color(245, 238, 235);    // Soft cream/beige

    public MainFrame() {
        this.currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            throw new IllegalStateException("Sesi user kosong. Harus login.");
        }

        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
        this.bottomNavPanel = new JPanel(new GridLayout(1, 4, 0, 0));

        initUI();
    }

    private void initUI() {
        setTitle("Woodify");
        // mobile viewport size matching LoginFrame aspect ratio
        setSize(390, 820);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new BorderLayout());

        // 1. Setup Global Top Bar
        setupTopBar();

        // 2. Setup Bottom Nav Bar
        setupBottomNav();

        // 3. Setup Content Panels
        setupContentPanels();

        add(contentPanel, BorderLayout.CENTER);
        add(bottomNavPanel, BorderLayout.SOUTH);

        // Load Halaman Pertama (Dashboard)
        showPanel("dashboard");
    }

    private void setupTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(COLOR_TOPBAR_BG);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(235, 225, 220)),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        topBar.setPreferredSize(new Dimension(390, 60));

        JLabel lblLogo = new JLabel("WOODIFY");
        lblLogo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblLogo.setForeground(new Color(74, 35, 17)); // Dark brown

        // Circular profile icon
        JButton btnProfile = new JButton("👤") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 225, 220));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnProfile.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        btnProfile.setContentAreaFilled(false);
        btnProfile.setBorderPainted(false);
        btnProfile.setFocusPainted(false);
        btnProfile.setPreferredSize(new Dimension(34, 34));
        btnProfile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnProfile.addActionListener(e -> showPanel("profil"));

        topBar.add(lblLogo, BorderLayout.WEST);
        topBar.add(btnProfile, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);
    }

    private void setupBottomNav() {
        bottomNavPanel.setBackground(COLOR_NAV_BG);
        bottomNavPanel.setPreferredSize(new Dimension(390, 75));
        bottomNavPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 220, 215)));

        // Create buttons
        navButtons.put("dashboard", new NavButton("dashboard", "⬜", "Beranda"));
        navButtons.put("manajemen_menu", new NavButton("manajemen_menu", "🗳️", "Manajemen"));
        navButtons.put("transaksi", new NavButton("transaksi", "🧾", "Transaksi"));
        navButtons.put("profil", new NavButton("profil", "👤", "Profil"));

        // Add to panel in sequence
        bottomNavPanel.add(navButtons.get("dashboard"));
        bottomNavPanel.add(navButtons.get("manajemen_menu"));
        bottomNavPanel.add(navButtons.get("transaksi"));
        bottomNavPanel.add(navButtons.get("profil"));
    }

    private void setupContentPanels() {
        panels.put("dashboard", new DashboardPanel());
        panels.put("manajemen_menu", new ManajemenMenuPanel(this::showPanel));
        panels.put("produk", new ProdukPanel());
        panels.put("pelanggan", new PelangganPanel());
        panels.put("transaksi", new TransaksiPanel());
        panels.put("profil", new ProfilPanel(this::handleLogout));

        if (currentUser.hasAccessToReports()) {
            panels.put("laporan", new LaporanPanel());
        }

        // Add panels to CardLayout container
        for (Map.Entry<String, BasePanel> entry : panels.entrySet()) {
            contentPanel.add(entry.getValue(), entry.getKey());
        }
    }

    public void showPanel(String panelName) {
        if (panelName.equals("transaksi") && currentUser.hasAccessToReports()) {
            panelName = "laporan";
        }
        cardLayout.show(contentPanel, panelName);

        // Update Bottom Nav state
        updateNavSelection(panelName);

        // Reload data
        BasePanel activePanel = panels.get(panelName);
        if (activePanel != null) {
            activePanel.onPageLoad();
        }
    }

    private void updateNavSelection(String activePanelName) {
        String highlightKey = activePanelName;
        // Group sub-pages of manajemen to manajemen tab highlight
        if (activePanelName.equals("produk") || activePanelName.equals("pelanggan")) {
            highlightKey = "manajemen_menu";
        } else if (activePanelName.equals("laporan")) {
            highlightKey = "transaksi";
        }

        for (Map.Entry<String, NavButton> entry : navButtons.entrySet()) {
            entry.getValue().setActive(entry.getKey().equals(highlightKey));
        }
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin logout?", "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    // --- NAV BUTTON CUSTOM COMPONENT ---
    private class NavButton extends JPanel {
        private final String targetName;
        private final String icon;
        private final String label;
        private boolean active;

        public NavButton(String targetName, String icon, String label) {
            this.targetName = targetName;
            this.icon = icon;
            this.label = label;
            this.active = false;

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showPanel(targetName);
                }
            });

            updateUIState();
        }

        public void setActive(boolean active) {
            this.active = active;
            updateUIState();
        }

        private void updateUIState() {
            removeAll();

            // Icon Panel (circular background if active)
            JPanel iconWrapper = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    if (active) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(74, 35, 17)); // Dark brown circle
                        int size = 38;
                        int x = (getWidth() - size) / 2;
                        int y = (getHeight() - size) / 2;
                        g2.fillOval(x, y, size, size);
                        g2.dispose();
                    }
                }
            };
            iconWrapper.setOpaque(false);
            iconWrapper.setPreferredSize(new Dimension(50, 40));
            iconWrapper.setMaximumSize(new Dimension(50, 40));
            iconWrapper.setLayout(new BorderLayout());

            JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
            lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            lblIcon.setForeground(active ? Color.WHITE : new Color(74, 35, 17));
            iconWrapper.add(lblIcon, BorderLayout.CENTER);

            // Label
            JLabel lblLabel = new JLabel(label, SwingConstants.CENTER);
            lblLabel.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 10));
            lblLabel.setForeground(new Color(74, 35, 17));
            lblLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            add(Box.createVerticalGlue());
            add(iconWrapper);
            add(Box.createRigidArea(new Dimension(0, 3)));
            add(lblLabel);
            add(Box.createVerticalGlue());

            revalidate();
            repaint();
        }
    }
}
