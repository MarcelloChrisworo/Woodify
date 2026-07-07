package com.woodify.view.dashboard;

import com.woodify.config.SessionManager;
import com.woodify.model.User;
import com.woodify.service.DashboardService;
import com.woodify.service.impl.DashboardServiceImpl;
import com.woodify.view.BasePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;
import java.util.function.Consumer;

public class CashierDashboardView extends BasePanel {

    private final DashboardService dashboardService;
    private final Consumer<String> navigator;

    // UI Elements
    private JLabel lblTotalTransaksi;

    // Colors matching the image
    private static final Color COLOR_BG = new Color(255, 248, 245);
    private static final Color COLOR_TEXT_DARK = new Color(74, 35, 17);
    private static final Color COLOR_TEXT_MUTED = new Color(100, 100, 100);
    private static final Color COLOR_CARD_BG = new Color(245, 238, 235);
    private static final Color COLOR_BTN_PRIMARY = new Color(7, 89, 69); // Dark green

    public CashierDashboardView(Consumer<String> navigator) {
        super("Cashier Dashboard");
        this.navigator = navigator;
        this.dashboardService = new DashboardServiceImpl();
        initUI();
    }

    private void initUI() {
        removeAll();
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(COLOR_BG);
        contentPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        contentPanel.add(buildHeader());
        addSpacer(contentPanel, 25);
        contentPanel.add(buildBigStatCard());
        addSpacer(contentPanel, 25);
        contentPanel.add(buildMulaiTransaksiButton());
        addSpacer(contentPanel, 25);
        contentPanel.add(buildSmallCards());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(COLOR_BG);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addSpacer(JPanel parent, int height) {
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(0, height));
        spacer.setMinimumSize(new Dimension(0, height));
        spacer.setMaximumSize(new Dimension(Short.MAX_VALUE, height));
        spacer.setAlignmentX(Component.CENTER_ALIGNMENT);
        parent.add(spacer);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(COLOR_BG);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        User user = SessionManager.getCurrentUser();
        String name = user != null ? user.getNamaLengkap() : "Sari Dewi";

        JLabel title = new JLabel("Selamat datang, " + name);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(COLOR_TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Shift Pagi • Kasir Utama");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(COLOR_TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0, 8)));
        header.add(subtitle);

        return header;
    }

    private JPanel buildBigStatCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(30, 20, 30, 20));
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 220));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("JUMLAH TRANSAKSI HARI INI");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitle.setForeground(COLOR_TEXT_DARK);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblTotalTransaksi = new JLabel("0");
        lblTotalTransaksi.setFont(new Font("SansSerif", Font.BOLD, 64));
        lblTotalTransaksi.setForeground(COLOR_TEXT_DARK);
        lblTotalTransaksi.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Target badge
        JPanel targetBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 220, 210));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.dispose();
            }
        };
        targetBadge.setOpaque(false);
        targetBadge.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JLabel lblTarget = new JLabel("📈 Target: 20");
        lblTarget.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblTarget.setForeground(COLOR_TEXT_DARK);
        targetBadge.add(lblTarget);
        targetBadge.setMaximumSize(new Dimension(120, 30));
        targetBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(lblTotalTransaksi);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(targetBadge);

        return card;
    }

    private JButton buildMulaiTransaksiButton() {
        JButton btnMulai = new JButton("Mulai Transaksi") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(COLOR_BTN_PRIMARY.darker());
                } else {
                    g2.setColor(COLOR_BTN_PRIMARY);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnMulai.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnMulai.setForeground(Color.WHITE);
        btnMulai.setContentAreaFilled(false);
        btnMulai.setBorderPainted(false);
        btnMulai.setFocusPainted(false);
        btnMulai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMulai.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnMulai.setMaximumSize(new Dimension(Short.MAX_VALUE, 55));
        btnMulai.setPreferredSize(new Dimension(350, 55));
        
        btnMulai.addActionListener(e -> {
            if (navigator != null) navigator.accept("transaksi");
        });

        return btnMulai;
    }

    private JPanel buildSmallCards() {
        JPanel container = new JPanel(new GridLayout(1, 2, 20, 0));
        container.setBackground(COLOR_BG);
        container.setMaximumSize(new Dimension(Short.MAX_VALUE, 120));
        container.setAlignmentX(Component.CENTER_ALIGNMENT);

        container.add(createSmallCard("📋", "Draft Tertunda", "2"));
        container.add(createSmallCard("🔄", "Retur", "0"));

        return container;
    }

    private JPanel createSmallCard(String iconStr, String title, String value) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(250, 245, 242));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.setColor(new Color(230, 220, 215));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15));
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Icon circle
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(90, 60, 50)); // Dark brown matching image
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(40, 40));
        iconPanel.setMaximumSize(new Dimension(40, 40));
        iconPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        iconPanel.setLayout(new BorderLayout());
        
        JLabel lblIcon = new JLabel(iconStr, SwingConstants.CENTER);
        lblIcon.setForeground(Color.WHITE);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconPanel.add(lblIcon, BorderLayout.CENTER);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblTitle.setForeground(COLOR_TEXT_MUTED);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblValue.setForeground(COLOR_TEXT_DARK);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(iconPanel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(lblValue);

        return card;
    }

    @Override
    public void onPageLoad() {
        try {
            Map<String, Object> stats = dashboardService.getDashboardStats();
            int trx = (int) stats.getOrDefault("transaksiHariIni", 0);
            lblTotalTransaksi.setText(String.valueOf(trx));
        } catch (Exception e) {
            lblTotalTransaksi.setText("0");
        }
    }
}
