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
        this(new PelangganServiceImpl());
    }

    public PelangganPanel(PelangganService pelangganService) {
        super("Manajemen Pelanggan");
        this.pelangganService = pelangganService;
        
        initUI();
    }

    private void initUI() {
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContent.setBackground(COLOR_BG_LIGHT);
        mainContent.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        // --- PANEL KIRI: DAFTAR PELANGGAN & PENCARIAN ---
        JPanel leftPanel = new JPanel(new BorderLayout(0, 12));
        leftPanel.setBackground(COLOR_BG_LIGHT);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 225, 225), 1, true),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        JLabel leftHeader = new JLabel("Daftar Pelanggan");
        leftHeader.setFont(FONT_SUBTITLE);
        leftHeader.setForeground(COLOR_PRIMARY);
        leftPanel.add(leftHeader, BorderLayout.NORTH);

        // Search Bar
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(COLOR_BG_LIGHT);
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(0, 34));
        txtSearch.setFont(FONT_REGULAR);
        JButton btnSearch = createStyledButton("Cari", COLOR_PRIMARY, COLOR_WHITE);
        btnSearch.addActionListener(e -> performSearch());
        
        txtSearch.addActionListener(e -> performSearch());

        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);

        JPanel leftCenter = new JPanel(new BorderLayout(0, 10));
        leftCenter.setOpaque(false);
        leftCenter.add(searchPanel, BorderLayout.NORTH);

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
    tblPelanggan.setRowHeight(28);
    tblPelanggan.setShowVerticalLines(false);
    tblPelanggan.setShowHorizontalLines(false);
    tblPelanggan.setIntercellSpacing(new Dimension(0, 1));
    tblPelanggan.setGridColor(new Color(235, 235, 235));
    tblPelanggan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225), 1, true));
        scrollPane.getViewport().setBackground(COLOR_WHITE);
        leftCenter.add(scrollPane, BorderLayout.CENTER);
        leftPanel.add(leftCenter, BorderLayout.CENTER);
        mainContent.add(leftPanel);

        // --- PANEL KANAN: FORM INPUT & AKSI ---
        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setBackground(COLOR_WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 225, 225), 1, true),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JPanel rightHeader = new JPanel(new GridLayout(2, 1, 0, 2));
        rightHeader.setBackground(COLOR_WHITE);
        JLabel rightTitle = new JLabel("Form Pelanggan");
        rightTitle.setFont(FONT_SUBTITLE);
        rightTitle.setForeground(COLOR_PRIMARY);
        JLabel rightSubtitle = new JLabel("Tambah, ubah, atau hapus data pelanggan dari panel ini.");
        rightSubtitle.setFont(FONT_REGULAR);
        rightSubtitle.setForeground(Color.GRAY);
        rightHeader.add(rightTitle);
        rightHeader.add(rightSubtitle);
        rightPanel.add(rightHeader, BorderLayout.NORTH);

        // Form Fields
        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setBackground(COLOR_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 6, 7, 6);
        gbc.weightx = 1.0;

        // Row 1: ID Pelanggan
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formGrid.add(createFormLabel("ID Pelanggan:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtId = createStyledTextField();
        txtId.setPreferredSize(new Dimension(240, 30));
        txtId.setEditable(false); // ID di-generate oleh DB (AUTO_INCREMENT)
        txtId.setBackground(COLOR_BG_LIGHT);
        formGrid.add(txtId, gbc);

        // Row 2: Nama
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        formGrid.add(createFormLabel("Nama Lengkap:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtNama = createStyledTextField();
        formGrid.add(txtNama, gbc);

        // Row 3: Nomor Telepon
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        formGrid.add(createFormLabel("No. Telepon:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtTelepon = createStyledTextField();
        formGrid.add(txtTelepon, gbc);

        // Row 4: Alamat
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formGrid.add(createFormLabel("Alamat:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtAlamat = createStyledTextArea();
        txtAlamat.setRows(4);
        txtAlamat.setColumns(20);
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        JScrollPane alamatScroll = new JScrollPane(txtAlamat);
        alamatScroll.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210), 1, true));
        alamatScroll.setPreferredSize(new Dimension(240, 92));
        formGrid.add(alamatScroll, gbc);

        JPanel formWrapper = new JPanel(new BorderLayout(0, 12));
        formWrapper.setBackground(COLOR_WHITE);
        formWrapper.add(formGrid, BorderLayout.NORTH);
        rightPanel.add(formWrapper, BorderLayout.CENTER);

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

        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setBackground(COLOR_WHITE);
        actionPanel.add(buttonPanel, BorderLayout.EAST);
        rightPanel.add(actionPanel, BorderLayout.SOUTH);
        mainContent.add(rightPanel);

        add(mainContent, BorderLayout.CENTER);
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BUTTON);
        label.setForeground(COLOR_TEXT_DARK);
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
