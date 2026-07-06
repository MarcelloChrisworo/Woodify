package com.woodify.view.transaction;

import com.woodify.config.SessionManager;
import com.woodify.exception.InsufficientStockException;
import com.woodify.exception.ValidationException;
import com.woodify.model.DetailTransaksi;
import com.woodify.model.Pelanggan;
import com.woodify.model.Produk;
import com.woodify.model.Transaksi;
import com.woodify.service.PelangganService;
import com.woodify.service.ProdukService;
import com.woodify.service.TransaksiService;
import com.woodify.service.PaymentService;
import com.woodify.service.impl.PelangganServiceImpl;
import com.woodify.service.impl.ProdukServiceImpl;
import com.woodify.service.impl.TransaksiServiceImpl;
import com.woodify.service.impl.PaymentServiceImpl;
import com.woodify.view.BasePanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransaksiPanel extends BasePanel {
    private final ProdukService produkService;
    private final PelangganService pelangganService;
    private final TransaksiService transaksiService;
    private final PaymentService paymentService;

    private JComboBox<Pelanggan> cbPelanggan;
    private JComboBox<Produk> cbProduk;
    private JSpinner spinQty;
    
    private JTable tblCart;
    private DefaultTableModel tableModel;
    
    private JLabel lblTotalBelanja;
    private JTextField txtCash;
    private JLabel lblChange;

    private final List<DetailTransaksi> cartItems = new ArrayList<>();
    private double totalBelanja = 0;
    private final NumberFormat rpFormat;

    public TransaksiPanel() {
        super("Transaksi Penjualan");
        this.produkService = new ProdukServiceImpl();
        this.pelangganService = new PelangganServiceImpl();
        this.transaksiService = new TransaksiServiceImpl();
        this.paymentService = new PaymentServiceImpl();
        this.rpFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        
        initUI();
    }

    private void initUI() {
        JPanel mainContent = new JPanel(new BorderLayout(15, 15));
        mainContent.setBackground(COLOR_BG_LIGHT);

        // --- PANEL UTARA: PEMILIHAN PRODUK & PELANGGAN ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(COLOR_WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        // Pelanggan
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(createFormLabel("Pelanggan:"), gbc);
        gbc.gridx = 1;
        cbPelanggan = new JComboBox<>();
        cbPelanggan.setPreferredSize(new Dimension(220, 28));
        inputPanel.add(cbPelanggan, gbc);

        // Produk
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(createFormLabel("Pilih Produk:"), gbc);
        gbc.gridx = 1;
        cbProduk = new JComboBox<>();
        inputPanel.add(cbProduk, gbc);

        // Qty
        gbc.gridx = 2; gbc.gridy = 1;
        inputPanel.add(createFormLabel("Qty:"), gbc);
        gbc.gridx = 3;
        spinQty = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spinQty.setPreferredSize(new Dimension(80, 28));
        inputPanel.add(spinQty, gbc);

        // Button Tambah Keranjang
        gbc.gridx = 4; gbc.gridy = 1;
        JButton btnAddCart = createStyledButton("Tambah", COLOR_SECONDARY, COLOR_TEXT_DARK);
        btnAddCart.addActionListener(e -> addToCart());
        inputPanel.add(btnAddCart, gbc);

        mainContent.add(inputPanel, BorderLayout.NORTH);

        // --- PANEL TENGAH: KERANJANG BELANJA (TABEL) ---
        JPanel cartPanel = new JPanel(new BorderLayout(0, 10));
        cartPanel.setBackground(COLOR_BG_LIGHT);

        String[] columns = {"ID Produk", "Nama Produk", "Harga Satuan", "Qty", "Subtotal"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblCart = new JTable(tableModel);
        tblCart.setFont(FONT_REGULAR);
        tblCart.setRowHeight(25);
        tblCart.getTableHeader().setBackground(COLOR_PRIMARY);
        tblCart.getTableHeader().setForeground(COLOR_WHITE);
        tblCart.getTableHeader().setFont(FONT_BUTTON);

        JScrollPane scrollPane = new JScrollPane(tblCart);
        cartPanel.add(scrollPane, BorderLayout.CENTER);

        // Tombol hapus item keranjang
        JPanel cartActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cartActionPanel.setBackground(COLOR_BG_LIGHT);
        JButton btnRemove = createStyledButton("Hapus Item Terpilih", COLOR_DANGER, COLOR_WHITE);
        btnRemove.addActionListener(e -> removeSelectedFromCart());
        cartActionPanel.add(btnRemove);
        cartPanel.add(cartActionPanel, BorderLayout.SOUTH);

        mainContent.add(cartPanel, BorderLayout.CENTER);

        // --- PANEL SELATAN: PEMBAYARAN & SUBMIT ---
        JPanel checkoutPanel = new JPanel(new GridBagLayout());
        checkoutPanel.setBackground(COLOR_WHITE);
        checkoutPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gbcTrx = new GridBagConstraints();
        gbcTrx.fill = GridBagConstraints.HORIZONTAL;
        gbcTrx.insets = new Insets(8, 10, 8, 10);

        // Total Belanja
        gbcTrx.gridx = 0; gbcTrx.gridy = 0;
        JLabel lblTotalTitle = new JLabel("TOTAL BELANJA:");
        lblTotalTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTotalTitle.setForeground(COLOR_PRIMARY);
        checkoutPanel.add(lblTotalTitle, gbcTrx);

        gbcTrx.gridx = 1;
        lblTotalBelanja = new JLabel("Rp 0");
        lblTotalBelanja.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTotalBelanja.setForeground(COLOR_TEXT_DARK);
        checkoutPanel.add(lblTotalBelanja, gbcTrx);

        // Input Bayar Cash
        gbcTrx.gridx = 0; gbcTrx.gridy = 1;
        checkoutPanel.add(createFormLabel("Jumlah Bayar Cash (Rp):"), gbcTrx);
        
        gbcTrx.gridx = 1;
        txtCash = new JTextField();
        txtCash.setFont(new Font("SansSerif", Font.BOLD, 14));
        txtCash.setPreferredSize(new Dimension(180, 30));
        txtCash.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateChange();
            }
        });
        checkoutPanel.add(txtCash, gbcTrx);

        // Kembalian
        gbcTrx.gridx = 0; gbcTrx.gridy = 2;
        checkoutPanel.add(createFormLabel("Uang Kembalian:"), gbcTrx);
        
        gbcTrx.gridx = 1;
        lblChange = new JLabel("Rp 0");
        lblChange.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblChange.setForeground(COLOR_SUCCESS);
        checkoutPanel.add(lblChange, gbcTrx);

        // Tombol Checkout
        gbcTrx.gridx = 2; gbcTrx.gridy = 0;
        gbcTrx.gridheight = 3;
        gbcTrx.fill = GridBagConstraints.BOTH;
        JButton btnSubmit = createStyledButton("BAYAR & CETAK NOTA", COLOR_PRIMARY, COLOR_WHITE);
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSubmit.addActionListener(e -> processCheckout());
        checkoutPanel.add(btnSubmit, gbcTrx);

        mainContent.add(checkoutPanel, BorderLayout.SOUTH);
        add(mainContent, BorderLayout.CENTER);
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BUTTON);
        label.setForeground(COLOR_PRIMARY);
        return label;
    }

    @Override
    public void onPageLoad() {
        // Load data Combo Boxes
        cbPelanggan.removeAllItems();
        for (Pelanggan c : pelangganService.getAllCustomers()) {
            cbPelanggan.addItem(c);
        }

        cbProduk.removeAllItems();
        for (Produk p : produkService.getAllProducts()) {
            if (p.getStok() > 0) {
                cbProduk.addItem(p);
            }
        }
        
        resetPanel();
    }

    private void addToCart() {
        Produk selectedProduk = (Produk) cbProduk.getSelectedItem();
        if (selectedProduk == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih produk terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int qty = (int) spinQty.getValue();
        
        // Cari apakah produk sudah ada di keranjang
        DetailTransaksi existingDetail = null;
        for (DetailTransaksi d : cartItems) {
            if (d.getProdukId().equals(selectedProduk.getId())) {
                existingDetail = d;
                break;
            }
        }

        int totalQtyRequested = qty;
        if (existingDetail != null) {
            totalQtyRequested += existingDetail.getQty();
        }

        // Cek kecukupan stok sebelum memasukkan keranjang
        if (selectedProduk.getStok() < totalQtyRequested) {
            JOptionPane.showMessageDialog(this, 
                    "Stok tidak mencukupi!\nSisa stok " + selectedProduk.getNama() + ": " + selectedProduk.getStok(), 
                    "Stok Kritis", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (existingDetail != null) {
            existingDetail.setQty(totalQtyRequested);
        } else {
            DetailTransaksi detail = new DetailTransaksi(selectedProduk.getId(), qty, selectedProduk.getHarga());
            detail.setProdukObj(selectedProduk);
            cartItems.add(detail);
        }

        refreshCartTable();
        spinQty.setValue(1); // reset qty ke 1
    }

    private void removeSelectedFromCart() {
        int row = tblCart.getSelectedRow();
        if (row != -1) {
            cartItems.remove(row);
            refreshCartTable();
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris di tabel keranjang yang ingin dihapus.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshCartTable() {
        tableModel.setRowCount(0);
        totalBelanja = 0;
        
        for (DetailTransaksi d : cartItems) {
            tableModel.addRow(new Object[]{
                    d.getProdukId(),
                    d.getProdukObj().getNama(),
                    rpFormat.format(d.getHargaJual()).replace("Rp", "Rp ").replace(",00", ""),
                    d.getQty(),
                    rpFormat.format(d.getSubtotal()).replace("Rp", "Rp ").replace(",00", "")
            });
            totalBelanja += d.getSubtotal();
        }

        lblTotalBelanja.setText(rpFormat.format(totalBelanja).replace("Rp", "Rp ").replace(",00", ""));
        calculateChange();
    }

    private void calculateChange() {
        String cashStr = txtCash.getText().replaceAll("[^0-9]", "");
        if (cashStr.isEmpty()) {
            lblChange.setText("Rp 0");
            return;
        }

        try {
            double cash = Double.parseDouble(cashStr);
            double change = cash - totalBelanja;
            if (change < 0) {
                lblChange.setText("Pembayaran Kurang: " + rpFormat.format(Math.abs(change)).replace("Rp", "Rp ").replace(",00", ""));
                lblChange.setForeground(COLOR_DANGER);
            } else {
                lblChange.setText(rpFormat.format(change).replace("Rp", "Rp ").replace(",00", ""));
                lblChange.setForeground(COLOR_SUCCESS);
            }
        } catch (NumberFormatException e) {
            lblChange.setText("Input Tidak Valid");
            lblChange.setForeground(COLOR_DANGER);
        }
    }

    private void processCheckout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang belanja kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cashStr = txtCash.getText().replaceAll("[^0-9]", "");
        if (cashStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan jumlah pembayaran cash.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double cash = Double.parseDouble(cashStr);
            if (cash < totalBelanja) {
                JOptionPane.showMessageDialog(this, "Uang pembayaran kurang dari total belanja.", "Gagal Bayar", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Pelanggan pelanggan = (Pelanggan) cbPelanggan.getSelectedItem();
            int pelangganId = pelanggan != null ? pelanggan.getId() : 1; // Default ke Pelanggan Umum (ID 1)

            // Setup Objek Transaksi
            Transaksi trx = new Transaksi();
            trx.setId(transaksiService.generateNewTransactionId());
            trx.setUserId(SessionManager.getCurrentUser().getId());
            trx.setPelangganId(pelangganId);
            trx.setPelangganObj(pelanggan);
            trx.setKasirObj(SessionManager.getCurrentUser());

            for (DetailTransaksi d : cartItems) {
                trx.addDetail(d);
            }

            paymentService.processPayment(trx, cash);

            // Simpan ke DB dengan transactional control
            transaksiService.processTransaction(trx);

            // Sukses! Tampilkan NotaDialog
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            NotaDialog notaDialog = new NotaDialog(parentWindow, trx);
            notaDialog.setVisible(true);

            // Reset Panel Transaksi
            onPageLoad();

        } catch (InsufficientStockException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Stok Habis / Kritis", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memproses checkout: " + ex.getMessage(), "Error Transaksi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void resetPanel() {
        cartItems.clear();
        tableModel.setRowCount(0);
        totalBelanja = 0;
        lblTotalBelanja.setText("Rp 0");
        txtCash.setText("");
        lblChange.setText("Rp 0");
        lblChange.setForeground(COLOR_SUCCESS);
        spinQty.setValue(1);
    }
}
