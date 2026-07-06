package com.woodify.view.dashboard;

import com.woodify.config.SessionManager;
import com.woodify.model.Produk;
import com.woodify.model.Transaksi;
import com.woodify.model.User;
import com.woodify.service.DashboardService;
import com.woodify.service.LaporanService;
import com.woodify.service.ProdukService;
import com.woodify.service.TransaksiService;
import com.woodify.service.impl.DashboardServiceImpl;
import com.woodify.service.impl.LaporanServiceImpl;
import com.woodify.service.impl.ProdukServiceImpl;
import com.woodify.service.impl.TransaksiServiceImpl;
import com.woodify.util.CurrencyUtil;
import com.woodify.util.DateUtil;
import com.woodify.view.BasePanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Panel Dashboard — menampilkan statistik ringkasan dan stok kritis.
 *
 * Sequence: DashboardPanel.onPageLoad()
 *   -> DashboardService.getDashboardStats()
 *   -> ProdukService.getCriticalStockProducts()
 *   -> TransaksiService.getAllTransactions() (5 terbaru)
 *   -> update UI labels & tables
 */
public class DashboardPanel extends BasePanel {

    private final DashboardService dashboardService;
    private final ProdukService    produkService;
    private final LaporanService   laporanService;
    private final TransaksiService transaksiService;

    // ─── Stat card labels ─────────────────────────────────────────────────
    private JLabel lblTotalProduk;
    private JLabel lblTotalPelanggan;
    private JLabel lblTotalTransaksi;
    private JLabel lblOmzet;

    // ─── Stok Kritis ──────────────────────────────────────────────────────
    private JLabel             lblCriticalCount;
    private DefaultTableModel  stockTableModel;

    // ─── Transaksi Terbaru ────────────────────────────────────────────────
    private DefaultTableModel  recentTableModel;

    // ─── Greeting ─────────────────────────────────────────────────────────
    private JLabel lblGreeting;

    public DashboardPanel() {
        super("Dashboard");
        this.dashboardService  = new DashboardServiceImpl();
        this.produkService     = new ProdukServiceImpl();
        this.laporanService    = new LaporanServiceImpl();
        this.transaksiService  = new TransaksiServiceImpl();
        initUI();
    }

    // ─── UI Build ─────────────────────────────────────────────────────────

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(COLOR_BG_LIGHT);

        // 1 — Greeting banner
        root.add(buildGreetingPanel(), BorderLayout.NORTH);

        // 2 — Content (scroll)
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(COLOR_BG_LIGHT);
        content.add(buildStatCardsPanel());
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        content.add(buildCriticalStockPanel());
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        content.add(buildRecentTransaksiPanel());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);

        add(root, BorderLayout.CENTER);
    }

    private JPanel buildGreetingPanel() {
        User user = SessionManager.getCurrentUser();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        String greeting = "Selamat datang, " + (user != null ? user.getNamaLengkap() : "User") + "!";
        lblGreeting = new JLabel(greeting);
        lblGreeting.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblGreeting.setForeground(Color.WHITE);

        String role = user != null ? user.getRoleDisplay() : "";
        JLabel lblRole = new JLabel(role);
        lblRole.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblRole.setForeground(COLOR_SECONDARY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setBackground(COLOR_PRIMARY);
        textPanel.add(lblGreeting);
        textPanel.add(lblRole);
        panel.add(textPanel, BorderLayout.CENTER);

        JLabel lblDate = new JLabel(DateUtil.formatDateOnly(new Date()));
        lblDate.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblDate.setForeground(COLOR_SECONDARY);
        lblDate.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblDate, BorderLayout.EAST);

        return panel;
    }

    private JPanel buildStatCardsPanel() {
        JPanel grid = new JPanel(new GridLayout(1, 4, 15, 0));
        grid.setBackground(COLOR_BG_LIGHT);
        grid.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Card 1: Total Produk
        JPanel c1 = createStatCard("Total Produk", "0", "📦", COLOR_PRIMARY);
        lblTotalProduk = (JLabel) c1.getClientProperty("valueLabel");

        // Card 2: Total Pelanggan
        JPanel c2 = createStatCard("Total Pelanggan", "0", "👥", COLOR_PRIMARY);
        lblTotalPelanggan = (JLabel) c2.getClientProperty("valueLabel");

        // Card 3: Total Transaksi
        JPanel c3 = createStatCard("Total Transaksi", "0", "🧾", COLOR_SECONDARY);
        lblTotalTransaksi = (JLabel) c3.getClientProperty("valueLabel");

        // Card 4: Omzet Bulan Ini (Owner only)
        JPanel c4 = createStatCard("Omzet Bulan Ini", "Rp 0", "💰", new Color(40, 167, 69));
        lblOmzet = (JLabel) c4.getClientProperty("valueLabel");

        grid.add(c1);
        grid.add(c2);
        grid.add(c3);
        grid.add(c4);
        return grid;
    }

    private JPanel buildCriticalStockPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(COLOR_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_DANGER, 1, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        // Header baris
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_WHITE);
        JLabel lblTitle = new JLabel("⚠  Stok Kritis (≤ 5 unit)");
        lblTitle.setFont(FONT_SUBTITLE);
        lblTitle.setForeground(COLOR_DANGER);
        lblCriticalCount = new JLabel("0 produk");
        lblCriticalCount.setFont(FONT_REGULAR);
        lblCriticalCount.setForeground(Color.GRAY);
        header.add(lblTitle, BorderLayout.WEST);
        header.add(lblCriticalCount, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // Tabel
        String[] cols = {"ID Produk", "Nama Produk", "Kategori", "Harga", "Sisa Stok"};
        stockTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = buildStyledTable(stockTableModel);
        JScrollPane sp = new JScrollPane(tbl);
        sp.setPreferredSize(new Dimension(0, 140));
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRecentTransaksiPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(COLOR_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY, 1, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel lblTitle = new JLabel("🕘  Transaksi Terbaru");
        lblTitle.setFont(FONT_SUBTITLE);
        lblTitle.setForeground(COLOR_PRIMARY);
        panel.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"No. Transaksi", "Tanggal", "Total Belanja"};
        recentTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = buildStyledTable(recentTableModel);
        JScrollPane sp = new JScrollPane(tbl);
        sp.setPreferredSize(new Dimension(0, 150));
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    // ─── Stat Card Helper ─────────────────────────────────────────────────

    private JPanel createStatCard(String title, String value, String icon, Color accent) {
        JPanel card = new JPanel(new BorderLayout(5, 8));
        card.setBackground(COLOR_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(15, 18, 15, 18)
        ));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblIcon.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_REGULAR);
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblValue.setForeground(COLOR_TEXT_DARK);

        // Accent bar (kiri)
        JPanel bar = new JPanel();
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(5, 0));

        JPanel textBox = new JPanel(new GridLayout(2, 1, 0, 4));
        textBox.setBackground(COLOR_WHITE);
        textBox.add(lblTitle);
        textBox.add(lblValue);

        card.add(bar, BorderLayout.WEST);
        card.add(textBox, BorderLayout.CENTER);
        card.add(lblIcon, BorderLayout.EAST);
        card.putClientProperty("valueLabel", lblValue);
        return card;
    }

    private JTable buildStyledTable(DefaultTableModel model) {
        JTable tbl = new JTable(model);
        tbl.setFont(FONT_REGULAR);
        tbl.setRowHeight(26);
        tbl.setShowVerticalLines(false);
        tbl.setGridColor(new Color(240, 240, 240));
        tbl.getTableHeader().setBackground(COLOR_PRIMARY);
        tbl.getTableHeader().setForeground(COLOR_WHITE);
        tbl.getTableHeader().setFont(FONT_BUTTON);
        return tbl;
    }

    // ─── Data Load ────────────────────────────────────────────────────────

    @Override
    public void onPageLoad() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            lblGreeting.setText("Selamat datang, " + user.getNamaLengkap() + "!");
        }

        // 1. Ambil statistik dari DashboardService
        try {
            Map<String, Object> stats = dashboardService.getDashboardStats();
            lblTotalProduk.setText(String.valueOf(stats.getOrDefault("totalProduk", 0)));
            lblTotalPelanggan.setText(String.valueOf(stats.getOrDefault("totalPelanggan", 0)));
            lblTotalTransaksi.setText(String.valueOf(stats.getOrDefault("totalTransaksi", 0)));

            // Polymorphism: Owner melihat omzet riil, Kasir melihat transaksi hari ini
            if (user != null && user.hasAccessToReports()) {
                double omzet = (double) stats.getOrDefault("omzetBulanIni", 0.0);
                lblOmzet.setText(CurrencyUtil.formatRupiah(omzet));
            } else {
                int todayTrx = (int) stats.getOrDefault("transaksiHariIni", 0);
                lblOmzet.setText(todayTrx + " trx hari ini");
            }
        } catch (Exception e) {
            lblTotalProduk.setText("–");
            lblTotalPelanggan.setText("–");
            lblTotalTransaksi.setText("–");
            lblOmzet.setText("–");
        }

        // 2. Stok Kritis
        try {
            List<Produk> critical = produkService.getCriticalStockProducts(5);
            lblCriticalCount.setText(critical.size() + " produk");
            stockTableModel.setRowCount(0);
            for (Produk p : critical) {
                stockTableModel.addRow(new Object[]{
                        p.getId(),
                        p.getNama(),
                        p.getKategori(),
                        CurrencyUtil.formatRupiah(p.getHarga()),
                        p.getStok()
                });
            }
        } catch (Exception e) {
            stockTableModel.setRowCount(0);
        }

        // 3. Transaksi Terbaru (5 terakhir)
        try {
            List<Transaksi> all = transaksiService.getAllTransactions();
            recentTableModel.setRowCount(0);
            int count = Math.min(5, all.size());
            for (int i = 0; i < count; i++) {
                Transaksi t = all.get(i);
                recentTableModel.addRow(new Object[]{
                        t.getId(),
                        DateUtil.format(t.getTanggal()),
                        CurrencyUtil.formatRupiah(t.getTotalHarga())
                });
            }
        } catch (Exception e) {
            recentTableModel.setRowCount(0);
        }
    }
}
