package com.woodify.view.transaction;

import com.woodify.model.Transaksi;
import com.woodify.service.ReceiptPrinter;
import com.woodify.service.impl.NotaServiceImpl;
import com.woodify.util.CurrencyUtil;
import com.woodify.util.DateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

/**
 * Dialog Nota / Struk Pembayaran.
 *
 * Menampilkan nota dalam format teks terformat setelah transaksi berhasil.
 * Menyediakan:
 *   - tombol Cetak (simulasi / clipboard)
 *   - tombol Tutup
 */
public class NotaDialog extends JDialog {

    private static final Color COLOR_PRIMARY   = new Color(27, 77, 62);
    private static final Color COLOR_SECONDARY = new Color(212, 163, 115);
    private static final Color COLOR_BG        = new Color(253, 252, 248);
    private static final Color COLOR_WHITE     = Color.WHITE;

    private final Transaksi transaksi;
    private final ReceiptPrinter receiptPrinter;
    private       JTextArea txtReceipt;

    public NotaDialog(Window owner, Transaksi transaksi) {
        this(owner, transaksi, new NotaServiceImpl());
    }

    public NotaDialog(Window owner, Transaksi transaksi, ReceiptPrinter receiptPrinter) {
        super(owner, "Struk Pembayaran – Woodify", ModalityType.APPLICATION_MODAL);
        this.transaksi = transaksi;
        this.receiptPrinter = receiptPrinter;
        initUI();
    }

    private void initUI() {
        setSize(460, 620);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setLayout(new BorderLayout());

        // ─── Header ───────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PRIMARY);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblTitle = new JLabel("TRANSAKSI BERHASIL  ✓");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(COLOR_WHITE);
        header.add(lblTitle, BorderLayout.WEST);

        JLabel lblDate = new JLabel(DateUtil.formatDateOnly(transaksi.getTanggal()));
        lblDate.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblDate.setForeground(COLOR_SECONDARY);
        header.add(lblDate, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ─── Receipt Body ─────────────────────────────────────────────────
        String receiptText = receiptPrinter.buildReceiptString(transaksi);

        txtReceipt = new JTextArea(receiptText);
        txtReceipt.setEditable(false);
        txtReceipt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReceipt.setBackground(COLOR_BG);
        txtReceipt.setBorder(new EmptyBorder(16, 20, 16, 20));
        txtReceipt.setLineWrap(false);

        JScrollPane scroll = new JScrollPane(txtReceipt);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        // ─── Summary Bar ──────────────────────────────────────────────────
        JPanel summaryBar = new JPanel(new GridLayout(1, 3, 0, 0));
        summaryBar.setBackground(new Color(240, 245, 242));
        summaryBar.setBorder(new EmptyBorder(8, 15, 8, 15));

        summaryBar.add(makeSummaryCell("Total", CurrencyUtil.formatRupiah(transaksi.getTotalHarga())));
        summaryBar.add(makeSummaryCell("Bayar", CurrencyUtil.formatRupiah(transaksi.getBayar())));
        summaryBar.add(makeSummaryCell("Kembalian", CurrencyUtil.formatRupiah(transaksi.getKembalian())));

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(COLOR_BG);
        centerWrapper.add(scroll, BorderLayout.CENTER);
        centerWrapper.add(summaryBar, BorderLayout.SOUTH);
        add(centerWrapper, BorderLayout.CENTER);

        // ─── Buttons ──────────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        btnPanel.setBackground(COLOR_WHITE);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        JButton btnCopy = new JButton("📋 Salin ke Clipboard");
        stylizeButton(btnCopy, new Color(80, 120, 100), COLOR_WHITE);
        btnCopy.addActionListener(e -> copyToClipboard());

        JButton btnPrint = new JButton("🖨 Cetak (Simulasi)");
        stylizeButton(btnPrint, COLOR_PRIMARY, COLOR_WHITE);
        btnPrint.addActionListener(e -> simulatePrint());

        JButton btnClose = new JButton("Tutup");
        stylizeButton(btnClose, Color.GRAY, COLOR_WHITE);
        btnClose.addActionListener(e -> dispose());

        btnPanel.add(btnCopy);
        btnPanel.add(btnPrint);
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────

    private JPanel makeSummaryCell(String label, String value) {
        JPanel cell = new JPanel(new GridLayout(2, 1, 0, 2));
        cell.setBackground(new Color(240, 245, 242));

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(Color.GRAY);

        JLabel val = new JLabel(value, SwingConstants.CENTER);
        val.setFont(new Font("SansSerif", Font.BOLD, 13));
        val.setForeground(COLOR_PRIMARY);

        cell.add(lbl);
        cell.add(val);
        return cell;
    }

    private void stylizeButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void copyToClipboard() {
        String text = txtReceipt.getText();
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(text), null);
        JOptionPane.showMessageDialog(this,
                "Nota berhasil disalin ke clipboard!", "Salin Berhasil",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void simulatePrint() {
        JOptionPane.showMessageDialog(this,
                "Simulasi cetak nota berhasil!\n" +
                "No. Nota : " + transaksi.getId() + "\n" +
                "Total    : " + CurrencyUtil.formatRupiah(transaksi.getTotalHarga()) + "\n\n" +
                "Pada implementasi production, data ini dikirim ke printer thermal.",
                "Cetak Struk", JOptionPane.INFORMATION_MESSAGE);
    }
}
