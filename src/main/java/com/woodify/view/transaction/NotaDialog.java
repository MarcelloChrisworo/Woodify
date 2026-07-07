package com.woodify.view.transaction;

import com.woodify.model.DetailTransaksi;
import com.woodify.model.Transaksi;
import com.woodify.service.ReceiptPrinter;
import com.woodify.service.impl.NotaServiceImpl;
import com.woodify.util.CurrencyUtil;
import com.woodify.util.DateUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class NotaDialog extends JDialog {

    private static final Color COLOR_BG = new Color(255, 248, 245);
    private static final Color COLOR_TEXT_DARK = new Color(74, 35, 17);
    private static final Color COLOR_TEXT_MUTED = new Color(130, 100, 90);
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_GREEN_CHECK = new Color(128, 226, 206); // Teal green checkmark circle

    private final Transaksi transaksi;

    public NotaDialog(Window owner, Transaksi transaksi) {
        super(owner, "Struk Pembayaran – Woodify", ModalityType.APPLICATION_MODAL);
        this.transaksi = transaksi;
        initUI();
    }

    private void initUI() {
        setSize(360, 740);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout());

        // Main panel scrolling if screen is too small
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Success Circle Badge & Header
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setOpaque(false);
        statusPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel circle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_GREEN_CHECK);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        circle.setOpaque(false);
        circle.setPreferredSize(new Dimension(54, 54));
        circle.setMaximumSize(new Dimension(54, 54));
        circle.setLayout(new BorderLayout());
        JLabel checkIcon = new JLabel("✓", SwingConstants.CENTER);
        checkIcon.setFont(new Font("SansSerif", Font.BOLD, 26));
        checkIcon.setForeground(new Color(7, 89, 69));
        circle.add(checkIcon, BorderLayout.CENTER);
        circle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSuccessTitle = new JLabel("Transaksi Berhasil", SwingConstants.CENTER);
        lblSuccessTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblSuccessTitle.setForeground(COLOR_TEXT_DARK);
        lblSuccessTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSuccessSub = new JLabel("Nota telah diterbitkan", SwingConstants.CENTER);
        lblSuccessSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSuccessSub.setForeground(COLOR_TEXT_MUTED);
        lblSuccessSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusPanel.add(circle);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        statusPanel.add(lblSuccessTitle);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        statusPanel.add(lblSuccessSub);

        mainPanel.add(statusPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Receipt White Card
        JPanel receiptCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(new Color(235, 225, 220));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };
        receiptCard.setLayout(new BoxLayout(receiptCard, BoxLayout.Y_AXIS));
        receiptCard.setOpaque(false);
        receiptCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        receiptCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Company Header Info
        JLabel lblComp = new JLabel("WOODIFY", SwingConstants.CENTER);
        lblComp.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblComp.setForeground(COLOR_TEXT_DARK);
        lblComp.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblAddr = new JLabel("Jl. Raya Kayu No. 123, Jepara", SwingConstants.CENTER);
        lblAddr.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblAddr.setForeground(COLOR_TEXT_MUTED);
        lblAddr.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPhone = new JLabel("Telp: (021) 555-0198", SwingConstants.CENTER);
        lblPhone.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblPhone.setForeground(COLOR_TEXT_MUTED);
        lblPhone.setAlignmentX(Component.CENTER_ALIGNMENT);

        receiptCard.add(lblComp);
        receiptCard.add(Box.createRigidArea(new Dimension(0, 4)));
        receiptCard.add(lblAddr);
        receiptCard.add(lblPhone);
        receiptCard.add(Box.createRigidArea(new Dimension(0, 15)));

        // Dashed line
        receiptCard.add(createDashedLine());
        receiptCard.add(Box.createRigidArea(new Dimension(0, 10)));

        // Transaction details meta
        receiptCard.add(createMetaRow("ID Transaksi", transaksi.getId(), true));
        receiptCard.add(Box.createRigidArea(new Dimension(0, 4)));
        String kasirName = transaksi.getKasirObj() != null ? transaksi.getKasirObj().getNamaLengkap() : "Kasir";
        receiptCard.add(createMetaRow("Kasir", kasirName, false));
        receiptCard.add(Box.createRigidArea(new Dimension(0, 4)));
        String pelName = transaksi.getPelangganObj() != null ? transaksi.getPelangganObj().getNama() : "Umum";
        receiptCard.add(createMetaRow("Pelanggan", pelName, false));
        receiptCard.add(Box.createRigidArea(new Dimension(0, 4)));

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("in", "ID"));
        String formattedDate = sdf.format(transaksi.getTanggal());
        receiptCard.add(createMetaRow("Tanggal", formattedDate, false));
        receiptCard.add(Box.createRigidArea(new Dimension(0, 10)));

        // Dashed line
        receiptCard.add(createDashedLine());
        receiptCard.add(Box.createRigidArea(new Dimension(0, 10)));

        // Purchased items list
        for (DetailTransaksi d : transaksi.getDetails()) {
            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setOpaque(false);
            itemPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));

            JPanel left = new JPanel();
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
            left.setOpaque(false);

            String pName = d.getProdukObj() != null ? d.getProdukObj().getNama() : d.getProdukId();
            JLabel lblItemName = new JLabel(pName);
            lblItemName.setFont(new Font("SansSerif", Font.BOLD, 12));
            lblItemName.setForeground(COLOR_TEXT_DARK);

            String formattedPrice = CurrencyUtil.formatRupiah(d.getHargaJual()).replace("Rp", "Rp ").replace(",00", "");
            JLabel lblQtyPrice = new JLabel(d.getQty() + " x " + formattedPrice);
            lblQtyPrice.setFont(new Font("SansSerif", Font.PLAIN, 10));
            lblQtyPrice.setForeground(COLOR_TEXT_MUTED);

            left.add(lblItemName);
            left.add(lblQtyPrice);

            String formattedSub = CurrencyUtil.formatRupiah(d.getSubtotal()).replace("Rp", "Rp ").replace(",00", "");
            JLabel lblSubtotal = new JLabel(formattedSub);
            lblSubtotal.setFont(new Font("SansSerif", Font.BOLD, 12));
            lblSubtotal.setForeground(COLOR_TEXT_DARK);

            itemPanel.add(left, BorderLayout.WEST);
            itemPanel.add(lblSubtotal, BorderLayout.EAST);

            receiptCard.add(itemPanel);
            receiptCard.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        receiptCard.add(Box.createRigidArea(new Dimension(0, 2)));
        // Dashed line
        receiptCard.add(createDashedLine());
        receiptCard.add(Box.createRigidArea(new Dimension(0, 10)));

        // Financial totals
        String formattedTotal = CurrencyUtil.formatRupiah(transaksi.getTotalHarga()).replace("Rp", "Rp ").replace(",00", "");
        receiptCard.add(createMetaRow("Total", formattedTotal, true));
        receiptCard.add(Box.createRigidArea(new Dimension(0, 4)));
        String formattedTunai = CurrencyUtil.formatRupiah(transaksi.getBayar()).replace("Rp", "Rp ").replace(",00", "");
        receiptCard.add(createMetaRow("Tunai", formattedTunai, false));
        receiptCard.add(Box.createRigidArea(new Dimension(0, 4)));
        String formattedKembali = CurrencyUtil.formatRupiah(transaksi.getKembalian()).replace("Rp", "Rp ").replace(",00", "");
        receiptCard.add(createMetaRow("Kembali", formattedKembali, false));

        receiptCard.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel lblFooterMsg = new JLabel("Terima kasih atas kunjungan Anda.", SwingConstants.CENTER);
        lblFooterMsg.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblFooterMsg.setForeground(COLOR_TEXT_MUTED);
        lblFooterMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        receiptCard.add(lblFooterMsg);

        mainPanel.add(receiptCard);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(COLOR_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Action Buttons at the Bottom
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setBackground(COLOR_BG);
        btnPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JButton btnPrint = new JButton("Cetak Nota") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_TEXT_DARK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnPrint.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setContentAreaFilled(false);
        btnPrint.setBorderPainted(false);
        btnPrint.setFocusPainted(false);
        btnPrint.setMaximumSize(new Dimension(Short.MAX_VALUE, 44));
        btnPrint.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPrint.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrint.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Simulasi Cetak Struk Berhasil!\nPrinter Thermal siap menerbitkan nota.", 
                "Cetak Sukses", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnClose = new JButton("Kembali ke Beranda") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(COLOR_TEXT_DARK);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnClose.setForeground(COLOR_TEXT_DARK);
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setFocusPainted(false);
        btnClose.setMaximumSize(new Dimension(Short.MAX_VALUE, 44));
        btnClose.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());

        btnPanel.add(btnPrint);
        btnPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        btnPanel.add(btnClose);

        add(btnPanel, BorderLayout.SOUTH);
    }

    private JPanel createDashedLine() {
        JPanel dash = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(210, 200, 195));
                float[] dashPattern = {4f, 4f};
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dashPattern, 0.0f));
                g2.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
                g2.dispose();
            }
        };
        dash.setOpaque(false);
        dash.setPreferredSize(new Dimension(0, 10));
        dash.setMaximumSize(new Dimension(Short.MAX_VALUE, 10));
        return dash;
    }

    private JPanel createMetaRow(String label, String value, boolean isBold) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblLabel.setForeground(COLOR_TEXT_MUTED);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", isBold ? Font.BOLD : Font.PLAIN, 12));
        lblValue.setForeground(COLOR_TEXT_DARK);

        row.add(lblLabel, BorderLayout.WEST);
        row.add(lblValue, BorderLayout.EAST);
        return row;
    }
}
