package com.woodify.view.customer;

import com.woodify.exception.ValidationException;
import com.woodify.model.Pelanggan;
import com.woodify.service.PelangganService;
import com.woodify.service.impl.PelangganServiceImpl;
import com.woodify.view.BasePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class PelangganPanel extends BasePanel {
    private final PelangganService pelangganService;

    // UI Colors
    private static final Color COLOR_BG = new Color(255, 248, 245);
    private static final Color COLOR_TEXT_DARK = new Color(74, 35, 17);
    private static final Color COLOR_TEXT_MUTED = new Color(130, 100, 90);
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_FAB_BG = new Color(0, 77, 64); // Dark Green #004D40
    private static final Color COLOR_FAB_HOVER = new Color(0, 96, 80);

    private JTextField txtSearch;
    private JPanel listContainer;
    private JButton btnFab;
    private JScrollPane scroll;

    public PelangganPanel() {
        this(new PelangganServiceImpl());
    }

    public PelangganPanel(PelangganService pelangganService) {
        super("Pelanggan");
        this.pelangganService = pelangganService;
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

        // 1. Title
        JLabel titleLabel = new JLabel("Pelanggan");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);

        addSpacer(contentPanel, 15);

        // 2. Rounded Search Field
        JPanel searchWrapper = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 230, 225));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
            }
        };
        searchWrapper.setOpaque(false);
        searchWrapper.setBorder(new EmptyBorder(6, 15, 6, 15));
        searchWrapper.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        searchWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setForeground(COLOR_TEXT_MUTED);

        txtSearch = new JTextField();
        txtSearch.setOpaque(false);
        txtSearch.setBorder(null);
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtSearch.setForeground(COLOR_TEXT_DARK);
        txtSearch.putClientProperty("JTextField.placeholderText", "Cari nama atau telepon");
        txtSearch.addActionListener(e -> performSearch());
        // Live search on key release
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                performSearch();
            }
        });

        searchWrapper.add(searchIcon, BorderLayout.WEST);
        searchWrapper.add(txtSearch, BorderLayout.CENTER);
        contentPanel.add(searchWrapper);

        addSpacer(contentPanel, 20);

        // 3. PALING SERING DIGUNAKAN Section
        JLabel labelFreq = new JLabel("PALING SERING DIGUNAKAN");
        labelFreq.setFont(new Font("SansSerif", Font.BOLD, 11));
        labelFreq.setForeground(COLOR_TEXT_MUTED);
        labelFreq.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(labelFreq);

        addSpacer(contentPanel, 10);

        // Pelanggan UMUM Card
        JPanel cardUmum = buildCustomerRowCard("👥", "Pelanggan UMUM", "Tanpa detail spesifik", "", true);
        cardUmum.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardUmum.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(PelangganPanel.this, 
                    "Pelanggan Umum adalah akun sistem default and tidak dapat diubah.", 
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        contentPanel.add(cardUmum);

        addSpacer(contentPanel, 25);

        // 4. Daftar Pelanggan Section
        JLabel labelList = new JLabel("Daftar Pelanggan");
        labelList.setFont(new Font("SansSerif", Font.BOLD, 14));
        labelList.setForeground(COLOR_TEXT_DARK);
        labelList.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(labelList);

        addSpacer(contentPanel, 10);

        // List Container
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);
        listContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(listContainer);

        // ScrollPane wraps content
        scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.setBackground(COLOR_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Add scroll to layered pane
        layeredPane.add(scroll, JLayeredPane.DEFAULT_LAYER);

        // 5. Floating Action Button (FAB)
        btnFab = new JButton("+ Tambah Pelanggan") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(COLOR_FAB_HOVER);
                } else {
                    g2.setColor(COLOR_FAB_BG);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnFab.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnFab.setForeground(Color.WHITE);
        btnFab.setContentAreaFilled(false);
        btnFab.setBorderPainted(false);
        btnFab.setFocusPainted(false);
        btnFab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFab.addActionListener(e -> showCustomerFormDialog(null));

        layeredPane.add(btnFab, JLayeredPane.PALETTE_LAYER);

        // Resize Listener for Responsive Bounds
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                scroll.setBounds(0, 0, w, h);

                int fabW = 160;
                int fabH = 46;
                btnFab.setBounds(w - fabW - 20, h - fabH - 20, fabW, fabH);
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

    private JPanel buildCustomerRowCard(String icon, String name, String phone, String address, boolean isSystemCard) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(new Color(232, 224, 218));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(15, 0));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 16, 12, 16));
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, isSystemCard ? 70 : 85));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Profile icon circle
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(90, 60, 50));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(42, 42));
        iconPanel.setLayout(new BorderLayout());
        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblIcon.setForeground(Color.WHITE);
        iconPanel.add(lblIcon, BorderLayout.CENTER);

        // Info Area
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblName.setForeground(COLOR_TEXT_DARK);

        info.add(Box.createVerticalGlue());
        info.add(lblName);

        if (isSystemCard) {
            JLabel lblDesc = new JLabel(phone);
            lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lblDesc.setForeground(COLOR_TEXT_MUTED);
            info.add(Box.createRigidArea(new Dimension(0, 2)));
            info.add(lblDesc);
            info.add(Box.createVerticalGlue());

            JLabel arrow = new JLabel("❯");
            arrow.setFont(new Font("SansSerif", Font.BOLD, 12));
            arrow.setForeground(COLOR_TEXT_MUTED);
            card.add(arrow, BorderLayout.EAST);
        } else {
            JLabel lblPhone = new JLabel(phone);
            lblPhone.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lblPhone.setForeground(COLOR_TEXT_DARK);

            JPanel topRow = new JPanel(new BorderLayout());
            topRow.setOpaque(false);
            topRow.add(lblName, BorderLayout.WEST);
            topRow.add(lblPhone, BorderLayout.EAST);

            JLabel lblAddress = new JLabel("<html>📍 " + address + "</html>");
            lblAddress.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lblAddress.setForeground(COLOR_TEXT_MUTED);

            info.removeAll();
            info.setLayout(new GridLayout(2, 1, 0, 4));
            info.add(topRow);
            info.add(lblAddress);
        }

        card.add(iconPanel, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);

        return card;
    }

    @Override
    public void onPageLoad() {
        txtSearch.setText("");
        loadCustomers(pelangganService.getAllCustomers());
    }

    private void loadCustomers(List<Pelanggan> list) {
        listContainer.removeAll();
        for (Pelanggan p : list) {
            if (p.getId() == 1) continue; // Skip Pelanggan Umum in bottom list

            JPanel card = buildCustomerRowCard("👤", p.getNama(), p.getTelepon(), p.getAlamat(), false);
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showCustomerFormDialog(p);
                }
            });
            listContainer.add(card);
            listContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        listContainer.revalidate();
        listContainer.repaint();
    }

    private void performSearch() {
        String keyword = txtSearch.getText();
        loadCustomers(pelangganService.searchCustomers(keyword));
    }

    // Modal dialog popup for Add / Edit
    private void showCustomerFormDialog(Pelanggan p) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow, p == null ? "Tambah Pelanggan" : "Ubah Pelanggan", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(350, 420);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel(p == null ? "Tambah Pelanggan" : "Ubah Pelanggan");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(COLOR_TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Inputs
        JLabel lblNama = new JLabel("Nama Lengkap");
        lblNama.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblNama.setForeground(COLOR_TEXT_MUTED);
        lblNama.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField txtNama = new JTextField(p == null ? "" : p.getNama());
        txtNama.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        txtNama.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTelepon = new JLabel("Nomor Telepon");
        lblTelepon.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTelepon.setForeground(COLOR_TEXT_MUTED);
        lblTelepon.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField txtTelepon = new JTextField(p == null ? "" : p.getTelepon());
        txtTelepon.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        txtTelepon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblAlamat = new JLabel("Alamat Lengkap");
        lblAlamat.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblAlamat.setForeground(COLOR_TEXT_MUTED);
        lblAlamat.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextArea txtAlamat = new JTextArea(4, 20);
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        txtAlamat.setText(p == null ? "" : p.getAlamat());
        txtAlamat.setBorder(BorderFactory.createLineBorder(new Color(230, 220, 215)));
        JScrollPane scrollArea = new JScrollPane(txtAlamat);
        scrollArea.setMaximumSize(new Dimension(Short.MAX_VALUE, 80));
        scrollArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblNama);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(txtNama);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(lblTelepon);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(txtTelepon);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        panel.add(lblAlamat);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(scrollArea);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Action Buttons Row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSave = createStyledButton("Simpan", COLOR_FAB_BG, Color.WHITE);
        btnSave.addActionListener(e -> {
            try {
                String nama = txtNama.getText().trim();
                String telepon = txtTelepon.getText().trim();
                String alamat = txtAlamat.getText().trim();

                if (nama.isEmpty() || telepon.isEmpty() || alamat.isEmpty()) {
                    throw new ValidationException("Semua field formulir harus diisi.");
                }

                if (p == null) {
                    Pelanggan newCustomer = new Pelanggan(0, nama, telepon, alamat);
                    pelangganService.addCustomer(newCustomer);
                } else {
                    Pelanggan updated = new Pelanggan(p.getId(), nama, telepon, alamat);
                    pelangganService.updateCustomer(updated);
                }
                dialog.dispose();
                onPageLoad();
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Gagal menyimpan data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        if (p != null) {
            JButton btnDelete = createStyledButton("Hapus", new Color(198, 40, 40), Color.WHITE);
            btnDelete.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(dialog, 
                    "Hapus pelanggan " + p.getNama() + "?", "Konfirmasi Hapus", 
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        pelangganService.deleteCustomer(p.getId());
                        dialog.dispose();
                        onPageLoad();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Gagal menghapus: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            btnRow.add(btnDelete);
        }

        JButton btnCancel = createStyledButton("Batal", Color.GRAY, Color.WHITE);
        btnCancel.addActionListener(e -> dialog.dispose());

        btnRow.add(btnCancel);
        btnRow.add(btnSave);

        panel.add(btnRow);

        dialog.add(panel);
        dialog.setVisible(true);
    }
}
