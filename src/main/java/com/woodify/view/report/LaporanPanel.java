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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Panel Laporan Penjualan.
 *
 * Sequence: LaporanPanel -> LaporanService -> TransaksiRepository/DB -> update UI
 * Fitur: filter rentang tanggal, ringkasan statistik, tabel transaksi,
 *        tombol "Lihat Detail" untuk detail item, export CSV placeholder.
 */
public class LaporanPanel extends BasePanel {

    private final LaporanService     laporanService;
    private final UserRepository     userRepository;
    private final PelangganRepository pelangganRepository;

    // ─── Filter ───────────────────────────────────────────────────────────
    private JSpinner spinStart;
    private JSpinner spinEnd;

    // ─── Summary cards ────────────────────────────────────────────────────
    private JLabel lblTotalIncome;
    private JLabel lblTotalTrx;
    private JLabel lblTotalItems;

    // ─── Tabel ────────────────────────────────────────────────────────────
    private JTable            tblReport;
    private DefaultTableModel tableModel;

    // ─── Data yang sudah diload ───────────────────────────────────────────
    private List<Transaksi> currentList;

    public LaporanPanel() {
        super("Laporan Penjualan");
        this.laporanService      = new LaporanServiceImpl();
        this.userRepository      = new UserRepositoryImpl();
        this.pelangganRepository = new PelangganRepositoryImpl();
        initUI();
    }

    // ─── Build UI ─────────────────────────────────────────────────────────

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, 15));
        root.setBackground(COLOR_BG_LIGHT);

        root.add(buildFilterPanel(),  BorderLayout.NORTH);
        root.add(buildCenterPanel(),  BorderLayout.CENTER);
        root.add(buildActionPanel(),  BorderLayout.SOUTH);

        add(root, BorderLayout.CENTER);
    }

    private JPanel buildFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(COLOR_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.MONTH, -1);
        Date oneMonthAgo = cal.getTime();

        spinStart = new JSpinner(new SpinnerDateModel(oneMonthAgo, null, null, Calendar.DAY_OF_MONTH));
        spinStart.setEditor(new JSpinner.DateEditor(spinStart, "dd-MM-yyyy"));
        spinStart.setPreferredSize(new Dimension(120, 28));

        spinEnd = new JSpinner(new SpinnerDateModel(today, null, null, Calendar.DAY_OF_MONTH));
        spinEnd.setEditor(new JSpinner.DateEditor(spinEnd, "dd-MM-yyyy"));
        spinEnd.setPreferredSize(new Dimension(120, 28));

        JButton btnFilter  = createStyledButton("🔍 Filter", COLOR_PRIMARY, COLOR_WHITE);
        JButton btnAllData = createStyledButton("Semua Data", COLOR_SECONDARY, COLOR_TEXT_DARK);

        btnFilter.addActionListener(e -> generateReport());
        btnAllData.addActionListener(e -> loadAllData());

        panel.add(new JLabel("Mulai:"));
        panel.add(spinStart);
        panel.add(new JLabel("Selesai:"));
        panel.add(spinEnd);
        panel.add(btnFilter);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(btnAllData);
        return panel;
    }

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(COLOR_BG_LIGHT);

        // Summary cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        cardsPanel.setBackground(COLOR_BG_LIGHT);

        JPanel c1 = createStatCard("Total Pendapatan", "Rp 0",  COLOR_PRIMARY);
        lblTotalIncome = (JLabel) c1.getClientProperty("valueLabel");

        JPanel c2 = createStatCard("Total Transaksi",  "0",     COLOR_PRIMARY);
        lblTotalTrx = (JLabel) c2.getClientProperty("valueLabel");

        JPanel c3 = createStatCard("Total Item Terjual", "0",   COLOR_SECONDARY);
        lblTotalItems = (JLabel) c3.getClientProperty("valueLabel");

        cardsPanel.add(c1);
        cardsPanel.add(c2);
        cardsPanel.add(c3);
        panel.add(cardsPanel, BorderLayout.NORTH);

        // Tabel
        JPanel tableWrapper = new JPanel(new BorderLayout(0, 5));
        tableWrapper.setBackground(COLOR_BG_LIGHT);
        tableWrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY, 1),
                "Riwayat Transaksi",
                0, 0, FONT_SUBTITLE, COLOR_PRIMARY));

        String[] cols = {"No. Transaksi", "Tanggal", "Kasir", "Pelanggan", "Total"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblReport = new JTable(tableModel);
        tblReport.setFont(FONT_REGULAR);
        tblReport.setRowHeight(26);
        tblReport.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblReport.setShowVerticalLines(false);
        tblReport.setGridColor(new Color(240, 240, 240));
        tblReport.getTableHeader().setBackground(COLOR_PRIMARY);
        tblReport.getTableHeader().setForeground(COLOR_WHITE);
        tblReport.getTableHeader().setFont(FONT_BUTTON);

        tableWrapper.add(new JScrollPane(tblReport), BorderLayout.CENTER);
        panel.add(tableWrapper, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        panel.setBackground(COLOR_BG_LIGHT);

        JButton btnDetail = createStyledButton("📋 Lihat Detail Transaksi", COLOR_PRIMARY, COLOR_WHITE);
        JButton btnExport = createStyledButton("📄 Export CSV (Placeholder)", Color.DARK_GRAY, COLOR_WHITE);

        btnDetail.addActionListener(e -> showSelectedDetail());
        btnExport.addActionListener(e -> DialogUtil.showInfo(this,
                "Fitur Export CSV akan tersedia di versi selanjutnya.\nSaat ini data dapat disalin dari tabel.",
                "Export CSV"));

        panel.add(btnDetail);
        panel.add(btnExport);
        return panel;
    }

    // ─── Card Helper ──────────────────────────────────────────────────────

    private JPanel createStatCard(String title, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(COLOR_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_REGULAR);
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblValue.setForeground(COLOR_TEXT_DARK);

        JPanel bar = new JPanel();
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(5, 0));

        card.add(bar, BorderLayout.WEST);
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        card.putClientProperty("valueLabel", lblValue);
        return card;
    }

    // ─── Data Logic ───────────────────────────────────────────────────────

    @Override
    public void onPageLoad() {
        Calendar cal = Calendar.getInstance();
        spinEnd.setValue(cal.getTime());
        cal.add(Calendar.MONTH, -1);
        spinStart.setValue(cal.getTime());
        generateReport();
    }

    private void generateReport() {
        Date rawStart = (Date) spinStart.getValue();
        Date rawEnd   = (Date) spinEnd.getValue();

        if (rawStart.after(rawEnd)) {
            DialogUtil.showWarning(this, "Tanggal mulai tidak boleh melebihi tanggal selesai.");
            return;
        }

        Date start = DateUtil.startOfDay(rawStart);
        Date end   = DateUtil.endOfDay(rawEnd);

        loadReport(start, end);
    }

    private void loadAllData() {
        // Set spinner ke rentang sangat lebar lalu query
        Calendar cal = Calendar.getInstance();
        Date end = DateUtil.endOfDay(cal.getTime());
        cal.add(Calendar.YEAR, -10);
        Date start = DateUtil.startOfDay(cal.getTime());
        loadReport(start, end);
    }

    private void loadReport(Date start, Date end) {
        // ─ Summary
        try {
            Map<String, Object> summary = laporanService.generateSummaryReport(start, end);
            double income = (double) summary.getOrDefault("totalPendapatan", 0.0);
            int    trxCnt = (int)    summary.getOrDefault("jumlahTransaksi", 0);
            int    items  = (int)    summary.getOrDefault("totalItemTerjual", 0);

            lblTotalIncome.setText(CurrencyUtil.formatRupiah(income));
            lblTotalTrx.setText(String.valueOf(trxCnt));
            lblTotalItems.setText(String.valueOf(items));
        } catch (Exception e) {
            lblTotalIncome.setText("–");
        }

        // ─ Tabel detail
        try {
            currentList = laporanService.generateDetailedReport(start, end);
            tableModel.setRowCount(0);

            for (Transaksi t : currentList) {
                // Lazy-load relasi kasir & pelanggan
                if (t.getKasirObj() == null) {
                    t.setKasirObj(userRepository.findById(t.getUserId()));
                }
                if (t.getPelangganObj() == null && t.getPelangganId() > 0) {
                    t.setPelangganObj(pelangganRepository.findById(t.getPelangganId()));
                }

                String kasir     = t.getKasirObj()     != null ? t.getKasirObj().getNamaLengkap()     : "ID:" + t.getUserId();
                String pelanggan = t.getPelangganObj() != null ? t.getPelangganObj().getNama() : "Umum";

                tableModel.addRow(new Object[]{
                        t.getId(),
                        DateUtil.format(t.getTanggal()),
                        kasir,
                        pelanggan,
                        CurrencyUtil.formatRupiah(t.getTotalHarga())
                });
            }
        } catch (Exception e) {
            tableModel.setRowCount(0);
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
        // Build detail string
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
        sp.setPreferredSize(new Dimension(480, 380));

        JOptionPane.showMessageDialog(this, sp, "Detail Transaksi", JOptionPane.PLAIN_MESSAGE);
    }
}
