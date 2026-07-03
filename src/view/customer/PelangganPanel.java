package com.woodify.view.customer;

import com.woodify.exception.ValidationException;
import com.woodify.model.Pelanggan;
import com.woodify.service.PelangganService;
import com.woodify.service.impl.PelangganServiceImpl;
import com.woodify.view.BasePanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PelangganPanel extends BasePanel {
    private final PelangganService pelangganService;

    private JTable tblPelanggan;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    // Form Input Fields
    private JTextField txtId;
    private JTextField txtNama;
    private JTextField txtTelepon;
    private JTextArea txtAlamat;

    private JButton btnTambah;
    private JButton btnUbah;
    private JButton btnHapus;
    private JButton btnClear;

    public PelangganPanel() {
        super("Manajemen Pelanggan");
        this.pelangganService = new PelangganServiceImpl();
        
        initUI();
    }

    private void initUI() {
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContent.setBackground(COLOR_BG_LIGHT);

        // --- PANEL KIRI: DAFTAR PELANGGAN & PENCARIAN ---
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
        String[] columns = {"ID", "Nama", "Telepon", "Alamat"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblPelanggan = new JTable(tableModel);
        tblPelanggan.setFont(FONT_REGULAR);
        tblPelanggan.setRowHeight(25);
        tblPelanggan.getTableHeader().setBackground(COLOR_PRIMARY);
        tblPelanggan.getTableHeader().setForeground(COLOR_WHITE);
        tblPelanggan.getTableHeader().setFont(FONT_BUTTON);

        // Row Selection Listener
        tblPelanggan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tblPelanggan.getSelectedRow();
                if (selectedRow != -1) {
                    loadSelectedCustomerToForm();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblPelanggan);
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

        // Row 1: ID Pelanggan
        gbc.gridx = 0; gbc.gridy = 0;
        formGrid.add(createFormLabel("ID Pelanggan:"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField();
        txtId.setPreferredSize(new Dimension(200, 25));
        txtId.setEditable(false); // ID di-generate oleh DB (AUTO_INCREMENT)
        txtId.setBackground(COLOR_BG_LIGHT);
        formGrid.add(txtId, gbc);

        // Row 2: Nama
        gbc.gridx = 0; gbc.gridy = 1;
        formGrid.add(createFormLabel("Nama Lengkap:"), gbc);
        gbc.gridx = 1;
        txtNama = new JTextField();
        formGrid.add(txtNama, gbc);

        // Row 3: Nomor Telepon
        gbc.gridx = 0; gbc.gridy = 2;
        formGrid.add(createFormLabel("No. Telepon:"), gbc);
        gbc.gridx = 1;
        txtTelepon = new JTextField();
        formGrid.add(txtTelepon, gbc);

        // Row 4: Alamat
        gbc.gridx = 0; gbc.gridy = 3;
        formGrid.add(createFormLabel("Alamat:"), gbc);
        gbc.gridx = 1;
        txtAlamat = new JTextArea(4, 20);
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        txtAlamat.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        formGrid.add(new JScrollPane(txtAlamat), gbc);

        rightPanel.add(formGrid, BorderLayout.CENTER);

        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(COLOR_WHITE);

        btnTambah = createStyledButton("Tambah Baru", COLOR_SUCCESS, COLOR_WHITE);
        btnUbah = createStyledButton("Simpan / Ubah", COLOR_PRIMARY, COLOR_WHITE);
        btnHapus = createStyledButton("Hapus", COLOR_DANGER, COLOR_WHITE);
        btnClear = createStyledButton("Clear", Color.GRAY, COLOR_WHITE);

        btnTambah.addActionListener(e -> handleAddCustomer());
        btnUbah.addActionListener(e -> handleUpdateCustomer());
        btnHapus.addActionListener(e -> handleDeleteCustomer());
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnTambah);
        buttonPanel.add(btnUbah);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnClear);

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainContent.add(rightPanel);

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
        loadCustomerTable(pelangganService.getAllCustomers());
        clearForm();
    }

    private void loadCustomerTable(List<Pelanggan> list) {
        tableModel.setRowCount(0);
        for (Pelanggan c : list) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getNama(),
                    c.getTelepon(),
                    c.getAlamat()
            });
        }
    }

    private void performSearch() {
        String keyword = txtSearch.getText();
        loadCustomerTable(pelangganService.searchCustomers(keyword));
    }

    private void loadSelectedCustomerToForm() {
        int selectedRow = tblPelanggan.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                Pelanggan c = pelangganService.getCustomerById(id);
                txtId.setText(String.valueOf(c.getId()));
                txtNama.setText(c.getNama());
                txtTelepon.setText(c.getTelepon());
                txtAlamat.setText(c.getAlamat());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleAddCustomer() {
        try {
            Pelanggan c = getCustomerFromInput();
            pelangganService.addCustomer(c);
            JOptionPane.showMessageDialog(this, "Pelanggan berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            onPageLoad();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menambah pelanggan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateCustomer() {
        try {
            String idStr = txtId.getText();
            if (idStr == null || idStr.trim().isEmpty()) {
                throw new ValidationException("Pilih pelanggan yang akan diubah terlebih dahulu.");
            }
            int id = Integer.parseInt(idStr);
            Pelanggan c = getCustomerFromInput();
            c.setId(id);
            pelangganService.updateCustomer(c);
            JOptionPane.showMessageDialog(this, "Pelanggan berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            onPageLoad();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui pelanggan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteCustomer() {
        String idStr = txtId.getText();
        if (idStr == null || idStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih pelanggan dari tabel terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idStr);
        if (id == 1) {
            JOptionPane.showMessageDialog(this, "Pelanggan Umum tidak boleh dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin menghapus pelanggan dengan ID '" + id + "'?", "Konfirmasi Hapus", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                pelangganService.deleteCustomer(id);
                JOptionPane.showMessageDialog(this, "Pelanggan berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                onPageLoad();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus pelanggan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Pelanggan getCustomerFromInput() {
        String nama = txtNama.getText();
        String telepon = txtTelepon.getText();
        String alamat = txtAlamat.getText();

        return new Pelanggan(nama, telepon, alamat);
    }

    private void clearForm() {
        txtId.setText("");
        txtNama.setText("");
        txtTelepon.setText("");
        txtAlamat.setText("");
        tblPelanggan.clearSelection();
    }
}
