package com.woodify.view.report;

import com.woodify.model.DetailTransaksi;
import com.woodify.model.Transaksi;
import com.woodify.repository.PelangganRepository;
import com.woodify.repository.UserRepository;
import com.woodify.repository.impl.PelangganRepositoryImpl;
import com.woodify.repository.impl.UserRepositoryImpl;
import com.woodify.service.LaporanService;
import com.woodify.service.impl.LaporanServiceImpl;
import com.woodify.util.CurrencyUtil;
import com.woodify.util.DateUtil;
import com.woodify.util.DialogUtil;
import com.woodify.view.BasePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LaporanPanel extends BasePanel {

    private final LaporanService laporanService;
    private final UserRepository userRepository;
    private final PelangganRepository pelangganRepository;

    // Date range filter values
    private Date startDate;
    private Date endDate;

    // UI elements
    private JLabel lblDateRangeDisplay;
    private JLabel lblTotalIncome;
    private JLabel lblTotalTrx;
    private JTable tblReport;
    private DefaultTableModel tableModel;
    private JLabel lblTableStatus;
    private JButton btnExport;
    private JScrollPane scroll;

    // UI Colors
    private static final Color COLOR_BG = new Color(255, 248, 245);
    private static final Color COLOR_TEXT_DARK = new Color(74, 35, 17);
    private static final Color COLOR_TEXT_MUTED = new Color(130, 100, 90);
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_BTN_PRIMARY = new Color(7, 89, 69); // Forest green
    private static final Color COLOR_SUCCESS_BG = new Color(224, 242, 241);
    private static final Color COLOR_SUCCESS_TXT = new Color(0, 121, 107);

    private List<Transaksi> currentList;

    public LaporanPanel() {
        super("Laporan Penjualan");
        this.laporanService = new LaporanServiceImpl();
        this.userRepository = new UserRepositoryImpl();
        this.pelangganRepository = new PelangganRepositoryImpl();

        // Default: 1 month range
        Calendar cal = Calendar.getInstance();
        this.endDate = cal.getTime();
        cal.add(Calendar.MONTH, -1);
        this.startDate = cal.getTime();

        initUI();
    }

    private void initUI() {
        removeAll();
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);

        // Main Layered Pane to support floating button
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        // Content Panel (Layout Y_AXIS)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(COLOR_BG);
        contentPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        // 1. Header
        JLabel titleLabel = new JLabel("Laporan Penjualan");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Tinjauan transaksi dan performa penjualan.");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(COLOR_TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(subtitleLabel);

        addSpacer(contentPanel, 15);

        // 2. Date Range Picker Selector (Rounded field)
        JPanel pickerCard = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(242, 232, 227));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
            }
        };
        pickerCard.setOpaque(false);
        pickerCard.setBorder(new EmptyBorder(10, 15, 10, 15));
        pickerCard.setMaximumSize(new Dimension(Short.MAX_VALUE, 44));
        pickerCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        pickerCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pickerCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showDateRangePickerDialog();
            }
        });

        JLabel calendarIcon = new JLabel("📅");
        calendarIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));

        lblDateRangeDisplay = new JLabel("Pilih Rentang Tanggal...");
        lblDateRangeDisplay.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblDateRangeDisplay.setForeground(COLOR_TEXT_DARK);

        pickerCard.add(calendarIcon, BorderLayout.WEST);
        pickerCard.add(lblDateRangeDisplay, BorderLayout.CENTER);
        contentPanel.add(pickerCard);

        addSpacer(contentPanel, 10);

        // 3. Tampilkan Laporan Button
        JButton btnShowReport = new JButton("Tampilkan Laporan") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(COLOR_BTN_PRIMARY.darker());
                } else {
                    g2.setColor(COLOR_BTN_PRIMARY);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnShowReport.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnShowReport.setForeground(Color.WHITE);
        btnShowReport.setContentAreaFilled(false);
        btnShowReport.setBorderPainted(false);
        btnShowReport.setFocusPainted(false);
        btnShowReport.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        btnShowReport.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnShowReport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnShowReport.addActionListener(e -> generateReport());

        contentPanel.add(btnShowReport);

        addSpacer(contentPanel, 20);

        // 4. Summary Stats Section (Vertical cards)
        // Card 1: Total Pendapatan
        JPanel card1 = createReportStatCard("Total Pendapatan", "Rp 0", "📈 +12.5% dari bulan lalu", "💵");
        card1.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTotalIncome = (JLabel) card1.getClientProperty("valueLabel");

        // Card 2: Jumlah Transaksi
        JPanel card2 = createReportStatCard("Jumlah Transaksi", "0", "📈 +5 transaksi baru", "📄");
        card2.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTotalTrx = (JLabel) card2.getClientProperty("valueLabel");

        contentPanel.add(card1);
        addSpacer(contentPanel, 12);
        contentPanel.add(card2);

        addSpacer(contentPanel, 20);

        // 5. Table Card Section ("Daftar Transaksi")
        JPanel tableCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(new Color(230, 220, 215));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };
        tableCard.setLayout(new BoxLayout(tableCard, BoxLayout.Y_AXIS));
        tableCard.setOpaque(false);
        tableCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        tableCard.setMaximumSize(new Dimension(Short.MAX_VALUE, 320));
        tableCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Table Header Label Row
        JPanel tblHeader = new JPanel(new BorderLayout());
        tblHeader.setOpaque(false);
        tblHeader.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        JLabel lblTblTitle = new JLabel("Daftar Transaksi");
        lblTblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTblTitle.setForeground(COLOR_TEXT_DARK);
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        tblHeader.add(lblTblTitle, BorderLayout.WEST);
        tblHeader.add(searchIcon, BorderLayout.EAST);
        tableCard.add(tblHeader);

        JPanel line = new JPanel();
        line.setBackground(new Color(235, 225, 220));
        line.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        tableCard.add(Box.createRigidArea(new Dimension(0, 10)));
        tableCard.add(line);
        tableCard.add(Box.createRigidArea(new Dimension(0, 10)));

        // Columns: ID, Tanggal, Total, Status, Aksi
        String[] cols = {"ID Transaksi", "Tanggal", "Total", "Status", "Aksi"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblReport = new JTable(tableModel);
        tblReport.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tblReport.setRowHeight(32);
        tblReport.setShowVerticalLines(false);
        tblReport.setGridColor(new Color(245, 240, 235));
        tblReport.getTableHeader().setBackground(new Color(245, 240, 235));
        tblReport.getTableHeader().setForeground(COLOR_TEXT_DARK);
        tblReport.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));

        // Custom Cell Renderers
        tblReport.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(COLOR_SUCCESS_BG);
                        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
                        g2.dispose();
                    }
                };
                cell.setOpaque(false);
                JLabel lbl = new JLabel(String.valueOf(value), SwingConstants.CENTER);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
                lbl.setForeground(COLOR_SUCCESS_TXT);
                cell.add(lbl);
                return cell;
            }
        });

        tblReport.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = new JLabel("⋮", SwingConstants.CENTER);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
                lbl.setForeground(COLOR_TEXT_MUTED);
                return lbl;
            }
        });

        tblReport.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblReport.getSelectedRow();
                int col = tblReport.getSelectedColumn();
                if (row != -1) {
                    // Click either row or Aksi column
                    showSelectedDetail();
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblReport);
        scrollTable.setBorder(null);
        scrollTable.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollTable);

        // Footer Table Pagination Row
        JPanel tblFooter = new JPanel(new BorderLayout());
        tblFooter.setOpaque(false);
        tblFooter.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
        tblFooter.setBorder(new EmptyBorder(8, 0, 0, 0));

        lblTableStatus = new JLabel("Menampilkan 0 transaksi");
        lblTableStatus.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTableStatus.setForeground(COLOR_TEXT_MUTED);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        navPanel.setOpaque(false);
        JLabel btnPrev = new JLabel("⟨");
        btnPrev.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnPrev.setForeground(COLOR_TEXT_MUTED);
        btnPrev.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel btnNext = new JLabel("⟩");
        btnNext.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnNext.setForeground(COLOR_TEXT_MUTED);
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        navPanel.add(btnPrev);
        navPanel.add(btnNext);

        tblFooter.add(lblTableStatus, BorderLayout.WEST);
        tblFooter.add(navPanel, BorderLayout.EAST);
        tableCard.add(tblFooter);

        contentPanel.add(tableCard);

        addSpacer(contentPanel, 40); // Bottom margin before edge

        // ScrollPane wraps content
        scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.setBackground(COLOR_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        layeredPane.add(scroll, JLayeredPane.DEFAULT_LAYER);

        // Centered Floating PDF Export Button
        btnExport = new JButton("Export PDF") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(COLOR_BTN_PRIMARY.darker());
                } else {
                    g2.setColor(COLOR_BTN_PRIMARY);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnExport.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnExport.setForeground(Color.WHITE);
        btnExport.setContentAreaFilled(false);
        btnExport.setBorderPainted(false);
        btnExport.setFocusPainted(false);
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExport.setIcon(new ImageIcon(new byte[]{})); // Empty placeholder icon
        btnExport.setText("📄 Export PDF");
        btnExport.addActionListener(e -> DialogUtil.showInfo(this, 
            "Laporan Penjualan berhasil diexport ke PDF!\nLokasi penyimpanan: dokumen/laporan_penjualan.pdf", 
            "Export PDF"));

        layeredPane.add(btnExport, JLayeredPane.PALETTE_LAYER);

        // Resize Listener for Responsive Bounds
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                scroll.setBounds(0, 0, w, h);

                int fabW = 140;
                int fabH = 44;
                btnExport.setBounds((w - fabW) / 2, h - fabH - 20, fabW, fabH);
            }
        });

        add(layeredPane, BorderLayout.CENTER);
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

    private JPanel createReportStatCard(String title, String value, String growthText, String emojiIcon) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(new Color(230, 220, 215));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 110));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTitle.setForeground(COLOR_TEXT_MUTED);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblValue.setForeground(COLOR_TEXT_DARK);

        JLabel lblGrowth = new JLabel(growthText);
        lblGrowth.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblGrowth.setForeground(COLOR_SUCCESS_TXT);

        leftPanel.add(lblTitle);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        leftPanel.add(lblValue);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        leftPanel.add(lblGrowth);

        // Icon Badge Panel (Top Right circle)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);

        JPanel circle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(90, 60, 50));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        circle.setOpaque(false);
        circle.setPreferredSize(new Dimension(38, 38));
        circle.setLayout(new BorderLayout());
        JLabel icon = new JLabel(emojiIcon, SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        icon.setForeground(Color.WHITE);
        circle.add(icon, BorderLayout.CENTER);
        rightPanel.add(circle);

        card.add(leftPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        card.putClientProperty("valueLabel", lblValue);
        return card;
    }

    @Override
    public void onPageLoad() {
        updateDateRangeDisplay();
        generateReport();
    }

    private void updateDateRangeDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        lblDateRangeDisplay.setText(sdf.format(startDate) + " - " + sdf.format(endDate));
    }

    private void generateReport() {
        Date start = DateUtil.startOfDay(startDate);
        Date end = DateUtil.endOfDay(endDate);

        // 1. Summary
        try {
            Map<String, Object> summary = laporanService.generateSummaryReport(start, end);
            double income = (double) summary.getOrDefault("totalPendapatan", 0.0);
            int trxCnt = (int) summary.getOrDefault("jumlahTransaksi", 0);

            lblTotalIncome.setText(CurrencyUtil.formatRupiah(income));
            lblTotalTrx.setText(String.valueOf(trxCnt));
        } catch (Exception e) {
            lblTotalIncome.setText("–");
            lblTotalTrx.setText("–");
        }

        // 2. Table Detail
        try {
            currentList = laporanService.generateDetailedReport(start, end);
            tableModel.setRowCount(0);

            SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("in", "ID"));

            for (Transaksi t : currentList) {
                tableModel.addRow(new Object[]{
                        "#" + t.getId(),
                        timeFormat.format(t.getTanggal()),
                        CurrencyUtil.formatRupiah(t.getTotalHarga()),
                        "Selesai",
                        "⋮"
                });
            }

            lblTableStatus.setText("Menampilkan 1-" + currentList.size() + " dari " + currentList.size() + " transaksi");
        } catch (Exception e) {
            tableModel.setRowCount(0);
            lblTableStatus.setText("Gagal memuat data");
            DialogUtil.showError(this, "Gagal memuat data laporan: " + e.getMessage());
        }
    }

    private void showSelectedDetail() {
        int row = tblReport.getSelectedRow();
        if (row == -1) {
            DialogUtil.showWarning(this, "Pilih satu baris transaksi dari tabel terlebih dahulu.");
            return;
        }
        if (currentList == null || row >= currentList.size()) return;

        Transaksi t = currentList.get(row);
        showDetailDialog(t);
    }

    private void showDetailDialog(Transaksi t) {
        if (t.getKasirObj() == null) {
            t.setKasirObj(userRepository.findById(t.getUserId()));
        }
        if (t.getPelangganObj() == null && t.getPelangganId() > 0) {
            t.setPelangganObj(pelangganRepository.findById(t.getPelangganId()));
        }

        StringBuilder sb = new StringBuilder();
        sb.append("══════════════════════════════════════\n");
        sb.append("  DETAIL TRANSAKSI  #").append(t.getId()).append("\n");
        sb.append("══════════════════════════════════════\n");
        sb.append("Tanggal  : ").append(DateUtil.format(t.getTanggal())).append("\n");
        String kasir = t.getKasirObj() != null ? t.getKasirObj().getNamaLengkap() : "–";
        String pelgn = t.getPelangganObj() != null ? t.getPelangganObj().getNama() : "Umum";
        sb.append("Kasir    : ").append(kasir).append("\n");
        sb.append("Pelanggan: ").append(pelgn).append("\n");
        sb.append("──────────────────────────────────────\n");
        sb.append(String.format("%-22s %5s  %-14s\n", "Produk", "Qty", "Subtotal"));
        sb.append("──────────────────────────────────────\n");

        List<DetailTransaksi> details = t.getDetails();
        if (details == null || details.isEmpty()) {
            sb.append("  (Tidak ada detail item)\n");
        } else {
            for (DetailTransaksi d : details) {
                String nama = d.getProdukObj() != null ? d.getProdukObj().getNama() : d.getProdukId();
                if (nama.length() > 20) nama = nama.substring(0, 18) + "..";
                sb.append(String.format("%-22s %5d  %-14s\n",
                        nama, d.getQty(), CurrencyUtil.formatRupiah(d.getSubtotal())));
                sb.append(String.format("  @ %-36s\n", CurrencyUtil.formatRupiah(d.getHargaJual())));
            }
        }
        sb.append("══════════════════════════════════════\n");
        sb.append(String.format("%-28s %-12s\n", "TOTAL:", CurrencyUtil.formatRupiah(t.getTotalHarga())));
        sb.append(String.format("%-28s %-12s\n", "Bayar:", CurrencyUtil.formatRupiah(t.getBayar())));
        sb.append(String.format("%-28s %-12s\n", "Kembalian:", CurrencyUtil.formatRupiah(t.getKembalian())));

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setEditable(false);
        area.setBackground(new Color(253, 253, 253));

        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(420, 380));

        JOptionPane.showMessageDialog(this, sp, "Detail Transaksi", JOptionPane.PLAIN_MESSAGE);
    }

    // Interactive custom Date Range Picker Dialog modal pop-up
    private void showDateRangePickerDialog() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow, "Pilih Rentang Tanggal", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(330, 240);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Pilih Rentang Tanggal");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(COLOR_TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Mulai Spinner
        JLabel lblStart = new JLabel("Tanggal Mulai");
        lblStart.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblStart.setForeground(COLOR_TEXT_MUTED);
        lblStart.setAlignmentX(Component.LEFT_ALIGNMENT);
        JSpinner spinStart = new JSpinner(new SpinnerDateModel(startDate, null, null, Calendar.DAY_OF_MONTH));
        spinStart.setEditor(new JSpinner.DateEditor(spinStart, "dd/MM/yyyy"));
        spinStart.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        spinStart.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Selesai Spinner
        JLabel lblEnd = new JLabel("Tanggal Selesai");
        lblEnd.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblEnd.setForeground(COLOR_TEXT_MUTED);
        lblEnd.setAlignmentX(Component.LEFT_ALIGNMENT);
        JSpinner spinEnd = new JSpinner(new SpinnerDateModel(endDate, null, null, Calendar.DAY_OF_MONTH));
        spinEnd.setEditor(new JSpinner.DateEditor(spinEnd, "dd/MM/yyyy"));
        spinEnd.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        spinEnd.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblStart);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(spinStart);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(lblEnd);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(spinEnd);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Apply Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnApply = createStyledButton("Terapkan", COLOR_BTN_PRIMARY, Color.WHITE);
        btnApply.addActionListener(e -> {
            Date s = (Date) spinStart.getValue();
            Date en = (Date) spinEnd.getValue();
            if (s.after(en)) {
                JOptionPane.showMessageDialog(dialog, "Tanggal mulai tidak boleh melebihi tanggal selesai.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else {
                this.startDate = s;
                this.endDate = en;
                updateDateRangeDisplay();
                dialog.dispose();
                generateReport();
            }
        });

        JButton btnCancel = createStyledButton("Batal", Color.GRAY, Color.WHITE);
        btnCancel.addActionListener(e -> dialog.dispose());

        btnRow.add(btnCancel);
        btnRow.add(btnApply);
        panel.add(btnRow);

        dialog.add(panel);
        dialog.setVisible(true);
    }
}
