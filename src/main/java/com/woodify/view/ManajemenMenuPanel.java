package com.woodify.view;

import com.woodify.config.SessionManager;
import com.woodify.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

public class ManajemenMenuPanel extends BasePanel {

    private final Consumer<String> navigator;
    private JPanel menuContainer;

    private static final Color COLOR_BG = new Color(255, 248, 245);
    private static final Color COLOR_TEXT_DARK = new Color(74, 35, 17);
    private static final Color COLOR_TEXT_MUTED = new Color(130, 100, 90);
    private static final Color COLOR_CARD_BG = Color.WHITE;

    public ManajemenMenuPanel(Consumer<String> navigator) {
        super("Manajemen");
        this.navigator = navigator;
        initUI();
    }

    private void initUI() {
        // Hapus default layout/components dari BasePanel
        removeAll();
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(COLOR_BG);
        contentPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        // Header Title
        JLabel titleLabel = new JLabel("Menu Manajemen");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Pilih modul manajemen yang ingin dibuka.");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(COLOR_TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Container for cards
        menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false);
        menuContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(menuContainer);

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.setBackground(COLOR_BG);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createMenuCard(String icon, String title, String desc, String target) {
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
        card.setLayout(new BorderLayout(15, 0));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        card.setMaximumSize(new Dimension(350, 85));
        card.setPreferredSize(new Dimension(350, 85));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icon circle
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(90, 60, 50));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(44, 44));
        iconPanel.setLayout(new BorderLayout());
        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        lblIcon.setForeground(Color.WHITE);
        iconPanel.add(lblIcon, BorderLayout.CENTER);

        // Text area
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTitle.setForeground(COLOR_TEXT_DARK);

        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblDesc.setForeground(COLOR_TEXT_MUTED);

        textPanel.add(Box.createVerticalGlue());
        textPanel.add(lblTitle);
        textPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        textPanel.add(lblDesc);
        textPanel.add(Box.createVerticalGlue());

        // Arrow label
        JLabel lblArrow = new JLabel("❯");
        lblArrow.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblArrow.setForeground(COLOR_TEXT_MUTED);

        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        card.add(lblArrow, BorderLayout.EAST);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (navigator != null) navigator.accept(target);
            }
        });

        return card;
    }

    @Override
    public void onPageLoad() {
        menuContainer.removeAll();

        User user = SessionManager.getCurrentUser();
        
        // Card 1: Manajemen Produk
        menuContainer.add(createMenuCard("📦", "Manajemen Produk", "Kelola data barang, harga, dan stok", "produk"));
        menuContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Card 2: Manajemen Pelanggan
        menuContainer.add(createMenuCard("👥", "Manajemen Pelanggan", "Kelola database pelanggan toko", "pelanggan"));
        
        // Card 3: Laporan Penjualan (Hanya untuk Owner)
        if (user != null && user.hasAccessToReports()) {
            menuContainer.add(Box.createRigidArea(new Dimension(0, 15)));
            menuContainer.add(createMenuCard("📊", "Laporan Penjualan", "Lihat laporan omzet dan transaksi", "laporan"));
        }

        menuContainer.revalidate();
        menuContainer.repaint();
    }
}
