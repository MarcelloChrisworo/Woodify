package com.woodify.view.product;

import com.woodify.exception.ValidationException;
import com.woodify.model.Produk;
import com.woodify.service.ProdukService;
import com.woodify.service.impl.ProdukServiceImpl;
import com.woodify.view.BasePanel;
import com.woodify.view.product.access.ProdukAccessPolicy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProdukPanel extends BasePanel {
    private final ProdukService produkService;

    private JTable tblProduk;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    
    // Form Input Fields
    private JTextField txtId;
    private JTextField txtNama;
    private JComboBox<String> cbKategori;
    private JTextField txtHarga;
    private JTextField txtStok;
    private JTextArea txtDeskripsi;

    private JButton btnTambah;
    private JButton btnUbah;
    private JButton btnHapus;
    private JButton btnClear;
    
    private final NumberFormat rpFormat;

    public ProdukPanel() {
        super("Manajemen Produk");
        this.produkService = new ProdukServiceImpl();
        this.rpFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        
        initUI();
    }

    private void initUI() {
        // Layout: Split Pane (Kiri: Tabel + Search, Kanan: Form input)
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContent.setBackground(COLOR_BG_LIGHT);

        // --- PANEL KIRI: DAFTAR PRODUK & PENCARIAN ---
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(COLOR_BG_LIGHT);

        // Search Bar
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(COLOR_BG_LIGHT);
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(0, 30));
        txtSearch.setFont(FONT_REGULAR);
        JButton btnSearch = createStyledButton("Cari", COLOR_PRIMARY, COLOR_WHITE);
        btnSearch.addActionListener(e -> performSearch());
        
        txtSearch.addActionListener(e -> performSearch());

        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        // Tabel
        String[] columns = {"ID", "Nama", "Kategori", "Harga", "Stok"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblProduk = new JTable(tableModel);
        tblProduk.setFont(FONT_REGULAR);
        tblProduk.setRowHeight(25);
        tblProduk.getTableHeader().setBackground(COLOR_PRIMARY);
        tblProduk.getTableHeader().setForeground(COLOR_WHITE);
        tblProduk.getTableHeader().setFont(FONT_BUTTON);

        // Row Selection Listener
        tblProduk.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tblProduk.getSelectedRow();
                if (selectedRow != -1) {
                    loadSelectedProductToForm();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblProduk);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        mainContent.add(leftPanel);

        // --- PANEL KANAN: FORM INPUT & AKSI ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(COLOR_WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Form Fields
        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setBackground(COLOR_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Row 1: ID Produk
        gbc.gridx = 0; gbc.gridy = 0;
        formGrid.add(createFormLabel("ID Produk:"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField();
        txtId.setPreferredSize(new Dimension(200, 25));
        formGrid.add(txtId, gbc);

        // Row 2: Nama Produk
        gbc.gridx = 0; gbc.gridy = 1;
        formGrid.add(createFormLabel("Nama Produk:"), gbc);
        gbc.gridx = 1;
        txtNama = new JTextField();
        formGrid.add(txtNama, gbc);

        // Row 3: Kategori
        gbc.gridx = 0; gbc.gridy = 2;
        formGrid.add(createFormLabel("Kategori:"), gbc);
        gbc.gridx = 1;
        cbKategori = new JComboBox<>(new String[]{"Kursi & Sofa", "Meja", "Lemari", "Dekorasi", "Lainnya"});
        formGrid.add(cbKategori, gbc);

        // Row 4: Harga
        gbc.gridx = 0; gbc.gridy = 3;
        formGrid.add(createFormLabel("Harga (Rp):"), gbc);
        gbc.gridx = 1;
        txtHarga = new JTextField();
        formGrid.add(txtHarga, gbc);

        // Row 5: Stok
        gbc.gridx = 0; gbc.gridy = 4;
        formGrid.add(createFormLabel("Stok Awal:"), gbc);
        gbc.gridx = 1;
        txtStok = new JTextField();
        formGrid.add(txtStok, gbc);

        // Row 6: Deskripsi
        gbc.gridx = 0; gbc.gridy = 5;
        formGrid.add(createFormLabel("Deskripsi:"), gbc);
        gbc.gridx = 1;
        txtDeskripsi = new JTextArea(4, 20);
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        txtDeskripsi.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        formGrid.add(new JScrollPane(txtDeskripsi), gbc);

        rightPanel.add(formGrid, BorderLayout.CENTER);

        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(COLOR_WHITE);

        btnTambah = createStyledButton("Tambah", COLOR_SUCCESS, COLOR_WHITE);
        btnUbah = createStyledButton("Simpan / Ubah", COLOR_PRIMARY, COLOR_WHITE);
        btnHapus = createStyledButton("Hapus", COLOR_DANGER, COLOR_WHITE);
        btnClear = createStyledButton("Clear", Color.GRAY, COLOR_WHITE);

        btnTambah.addActionListener(e -> handleAddProduct());
        btnUbah.addActionListener(e -> handleUpdateProduct());
        btnHapus.addActionListener(e -> handleDeleteProduct());
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnTambah);
        buttonPanel.add(btnUbah);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnClear);

        JPanel actionPanel = new JPanel(new BorderLayout(0, 8));
        actionPanel.setBackground(COLOR_WHITE);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setOpaque(false);

        actionPanel.add(buttonPanel, BorderLayout.CENTER);
        actionPanel.add(statusPanel, BorderLayout.SOUTH);

        rightPanel.add(actionPanel, BorderLayout.SOUTH);
        mainContent.add(rightPanel);

        add(mainContent, BorderLayout.CENTER);
        
        // PBO Access Control: Kasir tidak memiliki akses CRUD
        applyUserRolePermission(statusPanel);
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BUTTON);
        label.setForeground(COLOR_PRIMARY);
        return label;
    }

    private void applyUserRolePermission(JPanel statusPanel) {
        if (!ProdukAccessPolicy.canManageProducts()) {
            txtId.setEnabled(false);
            txtNama.setEnabled(false);
            cbKategori.setEnabled(false);
            txtHarga.setEnabled(false);
            txtStok.setEnabled(false);
            txtDeskripsi.setEnabled(false);
            
            btnTambah.setEnabled(false);
            btnUbah.setEnabled(false);
            btnHapus.setEnabled(false);
            
            JLabel lblWarning = new JLabel(ProdukAccessPolicy.getReadOnlyMessage());
            lblWarning.setForeground(COLOR_DANGER);
            lblWarning.setFont(new Font("SansSerif", Font.ITALIC, 11));
            statusPanel.add(lblWarning);
        }
    }

    @Override
    public void onPageLoad() {
        loadProductTable(produkService.getAllProducts());
        clearForm();
    }

    private void loadProductTable(List<Produk> list) {
        tableModel.setRowCount(0);
        for (Produk p : list) {
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getNama(),
                    p.getKategori(),
                    rpFormat.format(p.getHarga()).replace("Rp", "Rp ").replace(",00", ""),
                    p.getStok()
            });
        }
    }

    private void performSearch() {
        String keyword = txtSearch.getText();
        loadProductTable(produkService.searchProducts(keyword));
    }

    private void loadSelectedProductToForm() {
        int selectedRow = tblProduk.getSelectedRow();
        if (selectedRow != -1) {
            String id = tableModel.getValueAt(selectedRow, 0).toString();
            try {
                Produk p = produkService.getProductById(id);
                txtId.setText(p.getId());
                txtId.setEditable(false); // ID tidak boleh diedit
                txtNama.setText(p.getNama());
                cbKategori.setSelectedItem(p.getKategori());
                txtHarga.setText(String.format("%.0f", p.getHarga()));
                txtStok.setText(String.valueOf(p.getStok()));
                txtDeskripsi.setText(p.getDeskripsi());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleAddProduct() {
        try {
            Produk p = getProductFromInput();
            produkService.addProduct(p);
            JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            onPageLoad();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menambah produk: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateProduct() {
        try {
            Produk p = getProductFromInput();
            produkService.updateProduct(p);
            JOptionPane.showMessageDialog(this, "Produk berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            onPageLoad();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui produk: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteProduct() {
        String id = txtId.getText();
        if (id == null || id.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih produk dari tabel terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin menghapus produk '" + id + "'?", "Konfirmasi Hapus", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                produkService.deleteProduct(id);
                JOptionPane.showMessageDialog(this, "Produk berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                onPageLoad();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus produk: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Produk getProductFromInput() {
        String id = txtId.getText();
        String nama = txtNama.getText();
        String kategori = (String) cbKategori.getSelectedItem();
        String hargaStr = txtHarga.getText();
        String stokStr = txtStok.getText();
        String deskripsi = txtDeskripsi.getText();

        double harga;
        int stok;

        try {
            harga = Double.parseDouble(hargaStr);
        } catch (NumberFormatException e) {
            throw new ValidationException("Harga harus berupa angka valid.");
        }

        try {
            stok = Integer.parseInt(stokStr);
        } catch (NumberFormatException e) {
            throw new ValidationException("Stok harus berupa bilangan bulat valid.");
        }

        return new Produk(id, nama, kategori, harga, stok, deskripsi);
    }

    private void clearForm() {
        txtId.setText("");
        txtId.setEditable(true);
        txtNama.setText("");
        cbKategori.setSelectedIndex(0);
        txtHarga.setText("");
        txtStok.setText("");
        txtDeskripsi.setText("");
        tblProduk.clearSelection();
    }
}
