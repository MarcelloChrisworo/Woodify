package com.woodify.view.product;

import com.woodify.exception.ValidationException;
import com.woodify.model.Produk;
import com.woodify.service.ProdukService;
import com.woodify.service.impl.ProdukServiceImpl;
import com.woodify.view.BasePanel;
import com.woodify.view.product.access.ProdukAccessPolicy;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProdukPanel extends BasePanel {
    private final ProdukService produkService;
    private final NumberFormat rpFormat;

    // UI Colors
    private static final Color COLOR_BG = new Color(255, 248, 245);
    private static final Color COLOR_TEXT_DARK = new Color(74, 35, 17);
    private static final Color COLOR_TEXT_MUTED = new Color(130, 100, 90);
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_FAB_BG = new Color(0, 77, 64); // Dark Green #004D40
    private static final Color COLOR_FAB_HOVER = new Color(0, 96, 80);

    private static final Color COLOR_TEAL_BG = new Color(224, 242, 241); // Normal Stock Badge Bg
    private static final Color COLOR_TEAL_TXT = new Color(0, 121, 107);
    private static final Color COLOR_PINK_BG = new Color(255, 235, 235); // Critical Stock Badge Bg
    private static final Color COLOR_PINK_TXT = new Color(198, 40, 40);

    private JTextField txtSearch;
    private JPanel listContainer;
    private JButton btnFab;
    private JScrollPane scroll;

    public ProdukPanel() {
        super("Manajemen Produk");
        this.produkService = new ProdukServiceImpl();
        this.rpFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        initUI();
    }

    private void initUI() {
        removeAll();
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);

        // Layered pane for FAB
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        // Content Panel (Layout Y_AXIS)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(COLOR_BG);
        contentPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        // 1. Header
        JLabel titleLabel = new JLabel("Manajemen Produk");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Kelola inventaris dan katalog furnitur.");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(COLOR_TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(subtitleLabel);

        addSpacer(contentPanel, 15);

        // 2. Search and Filter row
        JPanel searchBarRow = new JPanel();
        searchBarRow.setLayout(new BoxLayout(searchBarRow, BoxLayout.X_AXIS));
        searchBarRow.setOpaque(false);
        searchBarRow.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        searchBarRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Search Field Rounded
        JPanel searchWrapper = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(242, 232, 227));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        searchWrapper.setOpaque(false);
        searchWrapper.setBorder(new EmptyBorder(6, 12, 6, 12));

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setForeground(COLOR_TEXT_MUTED);
        txtSearch = new JTextField();
        txtSearch.setOpaque(false);
        txtSearch.setBorder(null);
        txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtSearch.setForeground(COLOR_TEXT_DARK);
        txtSearch.putClientProperty("JTextField.placeholderText", "Cari kode atau nama...");
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

        // Filter button
        JButton btnFilter = new JButton("🎛️") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(250, 240, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(230, 220, 215));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        btnFilter.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        btnFilter.setContentAreaFilled(false);
        btnFilter.setBorderPainted(false);
        btnFilter.setFocusPainted(false);
        btnFilter.setPreferredSize(new Dimension(40, 40));
        btnFilter.setMaximumSize(new Dimension(40, 40));
        btnFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFilter.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Fitur filter kategori akan tersedia segera.", "Filter", JOptionPane.INFORMATION_MESSAGE);
        });

        searchBarRow.add(searchWrapper);
        searchBarRow.add(Box.createRigidArea(new Dimension(10, 0)));
        searchBarRow.add(btnFilter);

        contentPanel.add(searchBarRow);

        addSpacer(contentPanel, 20);

        // 3. Product Cards Vertical List
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

        layeredPane.add(scroll, JLayeredPane.DEFAULT_LAYER);

        // 4. Floating Action Button (FAB)
        btnFab = new JButton("+") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(COLOR_FAB_HOVER);
                } else {
                    g2.setColor(COLOR_FAB_BG);
                }
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnFab.setFont(new Font("SansSerif", Font.BOLD, 22));
        btnFab.setForeground(Color.WHITE);
        btnFab.setContentAreaFilled(false);
        btnFab.setBorderPainted(false);
        btnFab.setFocusPainted(false);
        btnFab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFab.addActionListener(e -> showProductFormDialog(null));

        layeredPane.add(btnFab, JLayeredPane.PALETTE_LAYER);

        // Hide FAB for Cashier (Read-only access)
        if (!ProdukAccessPolicy.canManageProducts()) {
            btnFab.setVisible(false);
        }

        // Resize Listener for Responsive Bounds
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                scroll.setBounds(0, 0, w, h);

                int fabSize = 52;
                btnFab.setBounds(w - fabSize - 20, h - fabSize - 20, fabSize, fabSize);
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

    private JPanel buildProductCard(Produk p) {
        boolean isCritical = p.getStok() <= 5;

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
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 260));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 1. Mockup Image Area (Gradient)
        JPanel imgArea = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw rounded top corners gradient
                GradientPaint gp = new GradientPaint(0, 0, new Color(245, 235, 230), 0, getHeight(), new Color(225, 215, 210));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() + 10, 16, 16));
                g2.dispose();
            }
        };
        imgArea.setOpaque(false);
        imgArea.setPreferredSize(new Dimension(0, 140));

        // Emoji display based on category
        String emoji = "🪵";
        if (p.getKategori() != null) {
            String cat = p.getKategori().toLowerCase();
            if (cat.contains("sofa")) emoji = "🛋️";
            else if (cat.contains("kursi")) emoji = "🪑";
            else if (cat.contains("meja")) emoji = "🪵";
            else if (cat.contains("lemari")) emoji = "🚪";
        }
        JLabel lblImg = new JLabel(emoji, SwingConstants.CENTER);
        lblImg.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        imgArea.add(lblImg, BorderLayout.CENTER);

        // Top badges row
        JPanel topBadgeRow = new JPanel(new BorderLayout());
        topBadgeRow.setOpaque(false);
        topBadgeRow.setBorder(new EmptyBorder(8, 12, 0, 12));

        // Status Badge (Normal/Kritis)
        JLabel lblStatus = new JLabel(isCritical ? "Kritis" : "Normal") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isCritical ? COLOR_PINK_BG : COLOR_TEAL_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblStatus.setForeground(isCritical ? COLOR_PINK_TXT : COLOR_TEAL_TXT);
        lblStatus.setBorder(new EmptyBorder(3, 8, 3, 8));
        lblStatus.setOpaque(false);

        topBadgeRow.add(lblStatus, BorderLayout.WEST);
        imgArea.add(topBadgeRow, BorderLayout.NORTH);

        card.add(imgArea, BorderLayout.NORTH);

        // 2. Info Area Below Image
        JPanel infoArea = new JPanel();
        infoArea.setLayout(new BoxLayout(infoArea, BoxLayout.Y_AXIS));
        infoArea.setOpaque(false);
        infoArea.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Category & Code Row
        JPanel catCodeRow = new JPanel(new BorderLayout());
        catCodeRow.setOpaque(false);
        JLabel lblCode = new JLabel(p.getId());
        lblCode.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblCode.setForeground(COLOR_TEXT_MUTED);

        JLabel lblCat = new JLabel(p.getKategori()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 238, 234));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblCat.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblCat.setForeground(COLOR_TEXT_MUTED);
        lblCat.setBorder(new EmptyBorder(2, 6, 2, 6));

        catCodeRow.add(lblCode, BorderLayout.WEST);
        catCodeRow.add(lblCat, BorderLayout.EAST);

        // Title Product
        JLabel lblTitle = new JLabel(p.getNama());
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(COLOR_TEXT_DARK);

        // Price & Stock Row
        JPanel priceStockRow = new JPanel(new BorderLayout());
        priceStockRow.setOpaque(false);

        JLabel lblPriceVal = new JLabel(rpFormat.format(p.getHarga()).replace("Rp", "Rp ").replace(",00", ""));
        lblPriceVal.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblPriceVal.setForeground(COLOR_TEXT_DARK);

        JLabel lblStockVal = new JLabel(p.getStok() + " Unit");
        lblStockVal.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblStockVal.setForeground(isCritical ? COLOR_PINK_TXT : COLOR_TEXT_DARK);

        JPanel priceLblPanel = new JPanel();
        priceLblPanel.setLayout(new BoxLayout(priceLblPanel, BoxLayout.Y_AXIS));
        priceLblPanel.setOpaque(false);
        JLabel lblPriceTitle = new JLabel("Harga Jual");
        lblPriceTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblPriceTitle.setForeground(COLOR_TEXT_MUTED);
        priceLblPanel.add(lblPriceTitle);
        priceLblPanel.add(lblPriceVal);

        JPanel stockLblPanel = new JPanel();
        stockLblPanel.setLayout(new BoxLayout(stockLblPanel, BoxLayout.Y_AXIS));
        stockLblPanel.setOpaque(false);
        JLabel lblStockTitle = new JLabel("Stok");
        lblStockTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblStockTitle.setForeground(COLOR_TEXT_MUTED);
        lblStockTitle.setAlignmentX(Component.RIGHT_ALIGNMENT);
        lblStockVal.setAlignmentX(Component.RIGHT_ALIGNMENT);
        stockLblPanel.add(lblStockTitle);
        stockLblPanel.add(lblStockVal);

        priceStockRow.add(priceLblPanel, BorderLayout.WEST);
        priceStockRow.add(stockLblPanel, BorderLayout.EAST);

        infoArea.add(catCodeRow);
        infoArea.add(Box.createRigidArea(new Dimension(0, 4)));
        infoArea.add(lblTitle);
        infoArea.add(Box.createRigidArea(new Dimension(0, 8)));
        infoArea.add(priceStockRow);

        card.add(infoArea, BorderLayout.CENTER);

        return card;
    }

    @Override
    public void onPageLoad() {
        txtSearch.setText("");
        loadProducts(produkService.getAllProducts());
    }

    private void loadProducts(List<Produk> list) {
        listContainer.removeAll();
        for (Produk p : list) {
            JPanel card = buildProductCard(p);
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showProductFormDialog(p);
                }
            });
            listContainer.add(card);
            listContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        listContainer.revalidate();
        listContainer.repaint();
    }

    private void performSearch() {
        String keyword = txtSearch.getText();
        loadProducts(produkService.searchProducts(keyword));
    }

    // popup form JDialog
    private void showProductFormDialog(Produk p) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        boolean canEdit = ProdukAccessPolicy.canManageProducts();

        JDialog dialog = new JDialog(parentWindow, p == null ? "Tambah Produk" : "Detail Produk", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(360, 480);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel(p == null ? "Tambah Produk" : (canEdit ? "Ubah Produk" : "Detail Produk (Read-Only)"));
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(COLOR_TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Inputs
        JLabel lblId = new JLabel("Kode Produk (ID)");
        lblId.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblId.setForeground(COLOR_TEXT_MUTED);
        lblId.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField txtId = new JTextField(p == null ? "" : p.getId());
        txtId.setEnabled(canEdit && p == null); // ID only editable during creation
        txtId.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        txtId.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNama = new JLabel("Nama Produk");
        lblNama.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblNama.setForeground(COLOR_TEXT_MUTED);
        lblNama.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField txtNama = new JTextField(p == null ? "" : p.getNama());
        txtNama.setEnabled(canEdit);
        txtNama.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        txtNama.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblKategori = new JLabel("Kategori");
        lblKategori.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblKategori.setForeground(COLOR_TEXT_MUTED);
        lblKategori.setAlignmentX(Component.LEFT_ALIGNMENT);
        JComboBox<String> cbKategori = new JComboBox<>(new String[]{"Sofa", "Kursi", "Meja", "Lemari"});
        cbKategori.setEnabled(canEdit);
        if (p != null) cbKategori.setSelectedItem(p.getKategori());
        cbKategori.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        cbKategori.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHarga = new JLabel("Harga Jual (Rp)");
        lblHarga.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblHarga.setForeground(COLOR_TEXT_MUTED);
        lblHarga.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField txtHarga = new JTextField(p == null ? "" : String.format("%.0f", p.getHarga()));
        txtHarga.setEnabled(canEdit);
        txtHarga.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        txtHarga.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblStok = new JLabel("Stok");
        lblStok.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblStok.setForeground(COLOR_TEXT_MUTED);
        lblStok.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField txtStok = new JTextField(p == null ? "" : String.valueOf(p.getStok()));
        txtStok.setEnabled(canEdit);
        txtStok.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        txtStok.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblId);
        panel.add(Box.createRigidArea(new Dimension(0, 3)));
        panel.add(txtId);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        panel.add(lblNama);
        panel.add(Box.createRigidArea(new Dimension(0, 3)));
        panel.add(txtNama);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        panel.add(lblKategori);
        panel.add(Box.createRigidArea(new Dimension(0, 3)));
        panel.add(cbKategori);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        panel.add(lblHarga);
        panel.add(Box.createRigidArea(new Dimension(0, 3)));
        panel.add(txtHarga);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        panel.add(lblStok);
        panel.add(Box.createRigidArea(new Dimension(0, 3)));
        panel.add(txtStok);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Action Buttons Row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (canEdit) {
            JButton btnSave = createStyledButton("Simpan", COLOR_FAB_BG, Color.WHITE);
            btnSave.addActionListener(e -> {
                try {
                    String id = txtId.getText().trim();
                    String nama = txtNama.getText().trim();
                    String kategori = (String) cbKategori.getSelectedItem();
                    String hargaStr = txtHarga.getText().trim();
                    String stokStr = txtStok.getText().trim();

                    if (id.isEmpty() || nama.isEmpty() || hargaStr.isEmpty() || stokStr.isEmpty()) {
                        throw new ValidationException("Semua field formulir harus diisi.");
                    }

                    double harga;
                    int stok;
                    try {
                        harga = Double.parseDouble(hargaStr);
                    } catch (NumberFormatException ex) {
                        throw new ValidationException("Harga harus berupa angka valid.");
                    }
                    try {
                        stok = Integer.parseInt(stokStr);
                    } catch (NumberFormatException ex) {
                        throw new ValidationException("Stok harus berupa bilangan bulat valid.");
                    }

                    if (p == null) {
                        Produk newProduct = new Produk(id, nama, kategori, harga, stok, "");
                        produkService.addProduct(newProduct);
                    } else {
                        Produk updated = new Produk(p.getId(), nama, kategori, harga, stok, p.getDeskripsi());
                        produkService.updateProduct(updated);
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
                        "Hapus produk " + p.getNama() + "?", "Konfirmasi Hapus", 
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            produkService.deleteProduct(p.getId());
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
        } else {
            JButton btnClose = createStyledButton("Tutup", COLOR_TEXT_DARK, Color.WHITE);
            btnClose.addActionListener(e -> dialog.dispose());
            btnRow.add(btnClose);
        }

        panel.add(btnRow);

        dialog.add(panel);
        dialog.setVisible(true);
    }
}
