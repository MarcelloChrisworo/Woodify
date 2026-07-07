package com.woodify.view.dashboard;

import com.woodify.config.SessionManager;
import com.woodify.model.Produk;
import com.woodify.model.User;
import com.woodify.service.DashboardService;
import com.woodify.service.ProdukService;
import com.woodify.service.impl.DashboardServiceImpl;
import com.woodify.service.impl.ProdukServiceImpl;
import com.woodify.util.CurrencyUtil;
import com.woodify.view.BasePanel;
import com.woodify.view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AdminDashboardView extends BasePanel {

    private final DashboardService dashboardService;
    private final ProdukService produkService;

    // UI Elements
    private JLabel lblTotalPenjualan;
    private JLabel lblTotalTransaksi;
    private JPanel panelStokKritisContainer;
    
    // Nav callback
    private final Consumer<String> navigator;

    // Colors matching the image
    private static final Color COLOR_BG = new Color(255, 248, 245);
    private static final Color COLOR_TEXT_DARK = new Color(74, 35, 17);
    private static final Color COLOR_TEXT_MUTED = new Color(130, 100, 90);
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_DANGER_BG = new Color(255, 220, 220);
    private static final Color COLOR_DANGER_TEXT = new Color(200, 0, 0);

    public AdminDashboardView(Consumer<String> navigator) {
        super("Admin Dashboard");
        this.navigator = navigator;
        this.dashboardService = new DashboardServiceImpl();
        this.produkService = new ProdukServiceImpl();
        
        initUI();
    }

    private void initUI() {
        removeAll();
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(COLOR_BG);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        contentPanel.add(buildHeader());
        addSpacer(contentPanel, 20);
        contentPanel.add(buildStatCards());
        addSpacer(contentPanel, 25);
        contentPanel.add(buildTopProducts());
        addSpacer(contentPanel, 25);
        contentPanel.add(buildStokKritis());
        addSpacer(contentPanel, 25);
        contentPanel.add(buildTrendChart());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(COLOR_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void addSpacer(JPanel parent, int height) {
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(0, height));
        spacer.setMinimumSize(new Dimension(0, height));
        spacer.setMaximumSize(new Dimension(Short.MAX_VALUE, height));
        spacer.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(spacer);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(COLOR_BG);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Short.MAX_VALUE, 65));

        User user = SessionManager.getCurrentUser();
        String name = user != null ? user.getNamaLengkap() : "Pak Budi";

        JLabel title = new JLabel("Selamat datang, " + name);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(COLOR_TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Berikut adalah ringkasan performa toko hari ini.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(COLOR_TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0, 5)));
        header.add(subtitle);

        return header;
    }

    private JPanel buildStatCards() {
        JPanel container = new JPanel(new GridLayout(2, 1, 0, 15));
        container.setBackground(COLOR_BG);
        container.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.setMaximumSize(new Dimension(Short.MAX_VALUE, 220));

        // Card 1: Penjualan
        JPanel card1 = createRoundedCard();
        card1.setLayout(new BoxLayout(card1, BoxLayout.Y_AXIS));
        
        JPanel header1 = new JPanel(new BorderLayout());
        header1.setOpaque(false);
        JLabel title1 = new JLabel("Total Penjualan Hari Ini");
        title1.setFont(new Font("SansSerif", Font.BOLD, 12));
        title1.setForeground(COLOR_TEXT_MUTED);
        JLabel icon1 = new JLabel("📈");
        header1.add(title1, BorderLayout.WEST);
        header1.add(icon1, BorderLayout.EAST);

        lblTotalPenjualan = new JLabel("Rp 0");
        lblTotalPenjualan.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTotalPenjualan.setForeground(COLOR_TEXT_DARK);

        JLabel lblGrowth = new JLabel("↑ +12% dari kemarin");
        lblGrowth.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblGrowth.setForeground(new Color(40, 160, 60));

        card1.add(header1);
        card1.add(Box.createRigidArea(new Dimension(0, 8)));
        card1.add(lblTotalPenjualan);
        card1.add(Box.createRigidArea(new Dimension(0, 5)));
        card1.add(lblGrowth);

        // Card 2: Transaksi
        JPanel card2 = createRoundedCard();
        card2.setLayout(new BoxLayout(card2, BoxLayout.Y_AXIS));

        JPanel header2 = new JPanel(new BorderLayout());
        header2.setOpaque(false);
        JLabel title2 = new JLabel("Jumlah Transaksi");
        title2.setFont(new Font("SansSerif", Font.BOLD, 12));
        title2.setForeground(COLOR_TEXT_MUTED);
        JLabel icon2 = new JLabel("🛍️");
        header2.add(title2, BorderLayout.WEST);
        header2.add(icon2, BorderLayout.EAST);

        lblTotalTransaksi = new JLabel("0");
        lblTotalTransaksi.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTotalTransaksi.setForeground(COLOR_TEXT_DARK);

        JLabel lblStatus = new JLabel("Transaksi berhasil");
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblStatus.setForeground(Color.GRAY);

        card2.add(header2);
        card2.add(Box.createRigidArea(new Dimension(0, 8)));
        card2.add(lblTotalTransaksi);
        card2.add(Box.createRigidArea(new Dimension(0, 5)));
        card2.add(lblStatus);

        container.add(card1);
        container.add(card2);

        return container;
    }

    private JPanel buildTopProducts() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(COLOR_BG);
        container.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_BG);
        header.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel title = new JLabel("Top 5 Produk Terlaris");
        title.setFont(new Font("SansSerif", Font.PLAIN, 18));
        title.setForeground(COLOR_TEXT_DARK);
        
        JLabel lblLihat = new JLabel("<html><u>Lihat Semua</u></html>");
        lblLihat.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblLihat.setForeground(Color.GRAY);
        lblLihat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLihat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (navigator != null) navigator.accept("laporan");
            }
        });

        header.add(title, BorderLayout.WEST);
        header.add(lblLihat, BorderLayout.EAST);

        // Horizontal scroll for products
        JPanel productList = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        productList.setBackground(COLOR_BG);
        
        // Mocking top 3 products for display
        productList.add(createProductCard("#1", "Sofa Jati Minimalis", "Terjual: 4 Unit", "Rp 4.500.000"));
        productList.add(createProductCard("#2", "Meja Makan Mahoni", "Terjual: 3 Unit", "Rp 6.200.000"));
        productList.add(createProductCard("#3", "Lemari Pakaian 3 Pintu", "Terjual: 2 Unit", "Rp 3.800.000"));

        JScrollPane scroll = new JScrollPane(productList);
        scroll.setBorder(null);
        scroll.setBackground(COLOR_BG);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setPreferredSize(new Dimension(350, 195));
        scroll.setMaximumSize(new Dimension(Short.MAX_VALUE, 195));

        container.add(header);
        
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setMaximumSize(new Dimension(Short.MAX_VALUE, 10));
        spacer.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(spacer);
        
        container.add(scroll);

        return container;
    }

    private JPanel createProductCard(String rank, String name, String sold, String price) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.setColor(new Color(220, 220, 220));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15));
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(140, 185));

        // Placeholder Image Area
        JPanel imgArea = new JPanel(new BorderLayout());
        imgArea.setBackground(new Color(230, 230, 230));
        imgArea.setPreferredSize(new Dimension(140, 90));
        
        JLabel lblImg = new JLabel("🖼️", SwingConstants.CENTER);
        lblImg.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        imgArea.add(lblImg, BorderLayout.CENTER);

        // Rank Badge
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
        badgePanel.setOpaque(false);
        JLabel lblRank = new JLabel(rank);
        lblRank.setOpaque(true);
        lblRank.setBackground(Color.WHITE);
        lblRank.setFont(new Font("SansSerif", Font.BOLD, 9));
        lblRank.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1, true),
            new EmptyBorder(1, 4, 1, 4)
        ));
        badgePanel.add(lblRank);
        imgArea.add(badgePanel, BorderLayout.NORTH);

        // Info Area
        JPanel infoArea = new JPanel();
        infoArea.setLayout(new BoxLayout(infoArea, BoxLayout.Y_AXIS));
        infoArea.setOpaque(false);
        infoArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblName.setForeground(COLOR_TEXT_DARK);

        JLabel lblSold = new JLabel(sold);
        lblSold.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblSold.setForeground(Color.GRAY);

        JLabel lblPrice = new JLabel(price);
        lblPrice.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblPrice.setForeground(COLOR_TEXT_DARK);

        infoArea.add(lblName);
        infoArea.add(Box.createRigidArea(new Dimension(0, 2)));
        infoArea.add(lblSold);
        infoArea.add(Box.createRigidArea(new Dimension(0, 6)));
        infoArea.add(lblPrice);

        card.add(imgArea, BorderLayout.NORTH);
        card.add(infoArea, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildStokKritis() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(COLOR_BG);
        container.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create rounded border panel
        JPanel outlineCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.setColor(new Color(220, 220, 220));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15));
                g2.dispose();
            }
        };
        outlineCard.setLayout(new BorderLayout());
        outlineCard.setOpaque(false);
        outlineCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        outlineCard.setMaximumSize(new Dimension(Short.MAX_VALUE, 320));
        outlineCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Title
        JLabel title = new JLabel("⚠️ Stok Kritis");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(COLOR_TEXT_DARK);

        // List Container
        panelStokKritisContainer = new JPanel();
        panelStokKritisContainer.setLayout(new BoxLayout(panelStokKritisContainer, BoxLayout.Y_AXIS));
        panelStokKritisContainer.setOpaque(false);
        
        // Button
        JButton btnKelola = new JButton("Kelola Stok") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(245, 240, 235));
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(220, 210, 205));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnKelola.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnKelola.setForeground(COLOR_TEXT_DARK);
        btnKelola.setContentAreaFilled(false);
        btnKelola.setBorderPainted(false);
        btnKelola.setFocusPainted(false);
        btnKelola.setBorder(new EmptyBorder(8, 20, 8, 20));
        btnKelola.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnKelola.addActionListener(e -> {
            if (navigator != null) navigator.accept("produk");
        });

        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setOpaque(false);
        btnPanel.add(btnKelola, BorderLayout.CENTER);

        JPanel innerLayout = new JPanel(new BorderLayout(0, 15));
        innerLayout.setOpaque(false);
        innerLayout.add(title, BorderLayout.NORTH);
        innerLayout.add(panelStokKritisContainer, BorderLayout.CENTER);
        innerLayout.add(btnPanel, BorderLayout.SOUTH);

        outlineCard.add(innerLayout, BorderLayout.CENTER);

        return outlineCard;
    }

    private JPanel createStokItem(String name, String sku, int qty) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(qty <= 5 && qty > 0 ? COLOR_DANGER_BG : new Color(245,245,245));
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(qty <= 5 ? new Color(255,180,180) : Color.LIGHT_GRAY, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        item.setMaximumSize(new Dimension(Short.MAX_VALUE, 60));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblName.setForeground(COLOR_TEXT_DARK);
        
        JLabel lblSku = new JLabel("SKU: " + sku);
        lblSku.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblSku.setForeground(COLOR_TEXT_MUTED);

        textPanel.add(lblName);
        textPanel.add(lblSku);

        JLabel lblQty = new JLabel(String.valueOf(qty));
        lblQty.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblQty.setForeground(qty <= 5 ? COLOR_DANGER_TEXT : COLOR_TEXT_DARK);

        item.add(textPanel, BorderLayout.CENTER);
        item.add(lblQty, BorderLayout.EAST);

        return item;
    }

    private JPanel buildTrendChart() {
        JPanel card = createRoundedCard();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(350, 220));
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 220));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Tren Penjualan (7 Hari)");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(COLOR_TEXT_DARK);
        JLabel dots = new JLabel("⋮");
        dots.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.add(title, BorderLayout.WEST);
        header.add(dots, BorderLayout.EAST);

        // Mock Chart Drawing
        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                int padding = 30;

                // Axes lines
                g2.setColor(new Color(230, 230, 230));
                g2.drawLine(padding, h - padding, w - padding, h - padding); // X
                
                // Labels Y
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.drawString("15Jt", 5, padding);
                g2.drawString("10Jt", 5, h/2);
                g2.drawString("5Jt", 5, h - padding - 20);

                // Bar data (Mock)
                int[] heights = {30, 50, 15, 70, 45, 60, 95};
                String[] days = {"Sab", "Rab", "Sen", "Sel", "Kam", "Jum", "Min"};
                int barWidth = (w - 2 * padding) / 7 - 8;
                
                for(int i=0; i<7; i++) {
                    int x = padding + 4 + i * (barWidth + 8);
                    int barH = heights[i];
                    int y = h - padding - barH;
                    
                    // Bar
                    if (i == 6) g2.setColor(new Color(74, 35, 17)); // Highlight last
                    else g2.setColor(new Color(255, 218, 193)); 
                    
                    g2.fillRect(x, y, barWidth, barH);
                    
                    // Day label
                    g2.setColor(Color.GRAY);
                    g2.drawString(days[i], x - 2, h - padding + 15);
                }
            }
        };
        chart.setOpaque(false);

        card.add(header, BorderLayout.NORTH);
        card.add(chart, BorderLayout.CENTER);

        return card;
    }

    private JPanel createRoundedCard() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.setColor(new Color(220, 220, 220));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));
        return p;
    }

    @Override
    public void onPageLoad() {
        // Load Stat
        try {
            Map<String, Object> stats = dashboardService.getDashboardStats();
            double omzet = (double) stats.getOrDefault("omzetBulanIni", 0.0);
            int trx = (int) stats.getOrDefault("transaksiHariIni", 0);
            
            lblTotalPenjualan.setText(CurrencyUtil.formatRupiah(omzet));
            lblTotalTransaksi.setText(String.valueOf(trx));
        } catch (Exception e) {
            lblTotalPenjualan.setText("Rp 0");
            lblTotalTransaksi.setText("0");
        }

        // Load Stok Kritis
        try {
            List<Produk> critical = produkService.getCriticalStockProducts(3);
            panelStokKritisContainer.removeAll();
            if (critical.isEmpty()) {
                JLabel lblEmpty = new JLabel("Tidak ada stok kritis.");
                lblEmpty.setFont(new Font("SansSerif", Font.PLAIN, 12));
                panelStokKritisContainer.add(lblEmpty);
            } else {
                for (Produk p : critical) {
                    panelStokKritisContainer.add(createStokItem(p.getNama(), "FUR-" + p.getId(), p.getStok()));
                    panelStokKritisContainer.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
            panelStokKritisContainer.revalidate();
            panelStokKritisContainer.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
