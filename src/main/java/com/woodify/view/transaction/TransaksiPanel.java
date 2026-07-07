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
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransaksiPanel extends BasePanel {
    private final ProdukService produkService;
    private final PelangganService pelangganService;
    private final TransaksiService transaksiService;
    private final PaymentService paymentService;

    // UI Colors
    private static final Color COLOR_BG = new Color(255, 248, 245);
    private static final Color COLOR_TEXT_DARK = new Color(74, 35, 17);
    private static final Color COLOR_TEXT_MUTED = new Color(130, 100, 90);
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_BTN_PRIMARY = new Color(7, 89, 69); // Forest green
    private static final Color COLOR_DANGER_RED = new Color(198, 40, 40);

    private JComboBox<Pelanggan> cbPelanggan;
    private JTextField txtProductSearch;
    private JPanel quickProductList;
    private JPanel cartListContainer;
    private JLabel lblTotalBelanja;
    private JTextField txtCash;
    private JLabel lblChange;

    private final List<DetailTransaksi> cartItems = new ArrayList<>();
    private double totalBelanja = 0;
    private final NumberFormat rpFormat;

    public TransaksiPanel() {
        super("Kasir");
        this.produkService = new ProdukServiceImpl();
        this.pelangganService = new PelangganServiceImpl();
        this.transaksiService = new TransaksiServiceImpl();
        this.paymentService = new PaymentServiceImpl();
        this.rpFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        
        initUI();
    }

    private void initUI() {
        removeAll();
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);

        // Scrollpane content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(COLOR_BG);
        contentPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        // 1. Title Label
        JLabel titleLabel = new JLabel("Kasir");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);

        addSpacer(contentPanel, 15);

        // 2. Pelanggan Selector Section
        JLabel lblPelangganTitle = new JLabel("Pelanggan");
        lblPelangganTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblPelangganTitle.setForeground(COLOR_TEXT_MUTED);
        lblPelangganTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblPelangganTitle);
        addSpacer(contentPanel, 4);

        cbPelanggan = new JComboBox<>();
        cbPelanggan.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cbPelanggan.setForeground(COLOR_TEXT_DARK);
        cbPelanggan.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        cbPelanggan.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbPelanggan.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Pelanggan) {
                    Pelanggan p = (Pelanggan) value;
                    if (p.getId() == 1) {
                        setText("👥 " + p.getNama().toUpperCase());
                    } else {
                        setText("👤 " + p.getNama());
                    }
                }
                return this;
            }
        });
        contentPanel.add(cbPelanggan);

        addSpacer(contentPanel, 15);

        // 3. Tambah Produk Search Section
        JLabel lblAddProduct = new JLabel("Tambah Produk");
        lblAddProduct.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblAddProduct.setForeground(COLOR_TEXT_MUTED);
        lblAddProduct.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblAddProduct);
        addSpacer(contentPanel, 4);

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
        searchWrapper.setMaximumSize(new Dimension(Short.MAX_VALUE, 38));
        searchWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setForeground(COLOR_TEXT_MUTED);
        txtProductSearch = new JTextField();
        txtProductSearch.setOpaque(false);
        txtProductSearch.setBorder(null);
        txtProductSearch.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtProductSearch.setForeground(COLOR_TEXT_DARK);
        txtProductSearch.putClientProperty("JTextField.placeholderText", "Cari nama atau SKU produk...");
        txtProductSearch.addActionListener(e -> performProductSearch());
        txtProductSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performProductSearch();
            }
        });
        searchWrapper.add(searchIcon, BorderLayout.WEST);
        searchWrapper.add(txtProductSearch, BorderLayout.CENTER);
        contentPanel.add(searchWrapper);

        addSpacer(contentPanel, 15);

        // 4. Quick horizontal scroll products
        quickProductList = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        quickProductList.setBackground(COLOR_BG);
        JScrollPane productScroll = new JScrollPane(quickProductList);
        productScroll.setBorder(null);
        productScroll.setBackground(COLOR_BG);
        productScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        productScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        productScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        productScroll.setPreferredSize(new Dimension(350, 160));
        productScroll.setMaximumSize(new Dimension(Short.MAX_VALUE, 160));
        contentPanel.add(productScroll);

        addSpacer(contentPanel, 20);

        // 5. Keranjang Title & Kosongkan Button Row
        JPanel cartHeaderRow = new JPanel(new BorderLayout());
        cartHeaderRow.setOpaque(false);
        cartHeaderRow.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        cartHeaderRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblCart = new JLabel("Keranjang");
        lblCart.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblCart.setForeground(COLOR_TEXT_DARK);

        JLabel lblClearCart = new JLabel("<html><u>Kosongkan</u></html>");
        lblClearCart.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblClearCart.setForeground(COLOR_DANGER_RED);
        lblClearCart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblClearCart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(TransaksiPanel.this, 
                    "Kosongkan keranjang belanja?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    resetPanel();
                }
            }
        });
        cartHeaderRow.add(lblCart, BorderLayout.WEST);
        cartHeaderRow.add(lblClearCart, BorderLayout.EAST);
        contentPanel.add(cartHeaderRow);

        addSpacer(contentPanel, 10);

        // Cart items container list
        cartListContainer = new JPanel();
        cartListContainer.setLayout(new BoxLayout(cartListContainer, BoxLayout.Y_AXIS));
        cartListContainer.setOpaque(false);
        cartListContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(cartListContainer);

        addSpacer(contentPanel, 20);

        // 6. Checkout Summary Area (Border Panel)
        JPanel checkoutCard = new JPanel() {
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
        checkoutCard.setLayout(new BoxLayout(checkoutCard, BoxLayout.Y_AXIS));
        checkoutCard.setOpaque(false);
        checkoutCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        checkoutCard.setMaximumSize(new Dimension(Short.MAX_VALUE, 260));
        checkoutCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Total Row
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.setMaximumSize(new Dimension(Short.MAX_VALUE, 35));
        JLabel lblTotalTitle = new JLabel("Total:");
        lblTotalTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTotalTitle.setForeground(COLOR_TEXT_DARK);
        lblTotalBelanja = new JLabel("Rp 0");
        lblTotalBelanja.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTotalBelanja.setForeground(COLOR_TEXT_DARK);
        totalRow.add(lblTotalTitle, BorderLayout.WEST);
        totalRow.add(lblTotalBelanja, BorderLayout.EAST);

        // Payment input row
        JPanel payInputPanel = new JPanel();
        payInputPanel.setLayout(new BoxLayout(payInputPanel, BoxLayout.Y_AXIS));
        payInputPanel.setOpaque(false);
        payInputPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 65));

        JLabel lblPayTitle = new JLabel("Bayar (Rp)");
        lblPayTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblPayTitle.setForeground(COLOR_TEXT_MUTED);

        txtCash = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(230, 220, 215));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txtCash.setFont(new Font("SansSerif", Font.BOLD, 20));
        txtCash.setForeground(COLOR_TEXT_DARK);
        txtCash.setHorizontalAlignment(JTextField.RIGHT);
        txtCash.setOpaque(false);
        txtCash.setBorder(new EmptyBorder(8, 12, 8, 12));
        txtCash.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateChange();
            }
        });
        payInputPanel.add(lblPayTitle);
        payInputPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        payInputPanel.add(txtCash);

        // Change container row
        JPanel changePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 240, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        changePanel.setOpaque(false);
        changePanel.setBorder(new EmptyBorder(10, 12, 10, 12));
        changePanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));

        JLabel lblChangeTitle = new JLabel("Kembalian:");
        lblChangeTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblChangeTitle.setForeground(COLOR_TEXT_MUTED);

        lblChange = new JLabel("Rp 0");
        lblChange.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblChange.setForeground(COLOR_TEXT_DARK);

        changePanel.add(lblChangeTitle, BorderLayout.WEST);
        changePanel.add(lblChange, BorderLayout.EAST);

        // Finish Checkout Button Stretched
        JButton btnCheckout = new JButton("✓ Selesaikan Transaksi") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(COLOR_BTN_PRIMARY.darker());
                } else {
                    g2.setColor(COLOR_BTN_PRIMARY);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCheckout.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setContentAreaFilled(false);
        btnCheckout.setBorderPainted(false);
        btnCheckout.setFocusPainted(false);
        btnCheckout.setMaximumSize(new Dimension(Short.MAX_VALUE, 44));
        btnCheckout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheckout.addActionListener(e -> processCheckout());

        checkoutCard.add(totalRow);
        checkoutCard.add(Box.createRigidArea(new Dimension(0, 12)));
        checkoutCard.add(payInputPanel);
        checkoutCard.add(Box.createRigidArea(new Dimension(0, 12)));
        checkoutCard.add(changePanel);
        checkoutCard.add(Box.createRigidArea(new Dimension(0, 15)));
        checkoutCard.add(btnCheckout);

        contentPanel.add(checkoutCard);

        // ScrollPane for entire checkout screen
        JScrollPane mainScroll = new JScrollPane(contentPanel);
        mainScroll.setBorder(null);
        mainScroll.setBackground(COLOR_BG);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(mainScroll, BorderLayout.CENTER);
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

    private JPanel buildQuickProductCard(Produk p) {
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
        card.setPreferredSize(new Dimension(140, 130));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Emoji graphic based on category
        String emoji = "🪵";
        if (p.getKategori() != null) {
            String cat = p.getKategori().toLowerCase();
            if (cat.contains("sofa")) emoji = "🛋️";
            else if (cat.contains("kursi")) emoji = "🪑";
            else if (cat.contains("lemari")) emoji = "🚪";
        }

        // Image component
        JPanel imageArea = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(248, 240, 236));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() + 5, 16, 16));
                g2.dispose();
            }
        };
        imageArea.setOpaque(false);
        imageArea.setPreferredSize(new Dimension(140, 80));

        JLabel lblImg = new JLabel(emoji, SwingConstants.CENTER);
        lblImg.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        imageArea.add(lblImg, BorderLayout.CENTER);

        // Stock badge overlay
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
        badgeRow.setOpaque(false);
        JLabel lblStock = new JLabel("Stok: " + p.getStok()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(225, 218, 214));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblStock.setFont(new Font("SansSerif", Font.PLAIN, 9));
        lblStock.setForeground(COLOR_TEXT_DARK);
        lblStock.setOpaque(false);
        lblStock.setBorder(new EmptyBorder(1, 4, 1, 4));
        badgeRow.add(lblStock);
        imageArea.add(badgeRow, BorderLayout.NORTH);

        // Info Area
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel lblName = new JLabel(p.getNama());
        lblName.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblName.setForeground(COLOR_TEXT_DARK);

        JLabel lblPrice = new JLabel(rpFormat.format(p.getHarga()).replace("Rp", "Rp ").replace(",00", ""));
        lblPrice.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblPrice.setForeground(COLOR_TEXT_MUTED);

        info.add(lblName);
        info.add(Box.createRigidArea(new Dimension(0, 2)));
        info.add(lblPrice);

        card.add(imageArea, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addProductToCart(p);
            }
        });

        return card;
    }

    private JPanel buildCartItemCard(DetailTransaksi d) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(new Color(238, 230, 226));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 15, 10, 15));
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 65));

        // Product text description
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblName = new JLabel(d.getProdukObj().getNama());
        lblName.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblName.setForeground(COLOR_TEXT_DARK);

        JLabel lblPrice = new JLabel(rpFormat.format(d.getHargaJual()).replace("Rp", "Rp ").replace(",00", ""));
        lblPrice.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblPrice.setForeground(COLOR_TEXT_MUTED);

        textPanel.add(Box.createVerticalGlue());
        textPanel.add(lblName);
        textPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        textPanel.add(lblPrice);
        textPanel.add(Box.createVerticalGlue());

        // Control Panel (+ and -)
        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        ctrl.setOpaque(false);

        // Circular minus button
        JButton btnMinus = new JButton("−") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(220, 210, 205));
                } else {
                    g2.setColor(new Color(235, 228, 224));
                }
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnMinus.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnMinus.setForeground(COLOR_TEXT_DARK);
        btnMinus.setContentAreaFilled(false);
        btnMinus.setBorderPainted(false);
        btnMinus.setFocusPainted(false);
        btnMinus.setPreferredSize(new Dimension(28, 28));
        btnMinus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMinus.addActionListener(e -> {
            int newQty = d.getQty() - 1;
            if (newQty <= 0) {
                cartItems.remove(d);
            } else {
                d.setQty(newQty);
            }
            refreshCartList();
        });

        // Quantity Label
        JLabel lblQty = new JLabel(String.valueOf(d.getQty()), SwingConstants.CENTER);
        lblQty.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblQty.setForeground(COLOR_TEXT_DARK);
        lblQty.setPreferredSize(new Dimension(24, 28));

        // Circular plus button
        JButton btnPlus = new JButton("+") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(220, 210, 205));
                } else {
                    g2.setColor(new Color(235, 228, 224));
                }
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnPlus.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnPlus.setForeground(COLOR_TEXT_DARK);
        btnPlus.setContentAreaFilled(false);
        btnPlus.setBorderPainted(false);
        btnPlus.setFocusPainted(false);
        btnPlus.setPreferredSize(new Dimension(28, 28));
        btnPlus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPlus.addActionListener(e -> {
            int newQty = d.getQty() + 1;
            if (d.getProdukObj().getStok() < newQty) {
                JOptionPane.showMessageDialog(TransaksiPanel.this, 
                    "Stok tidak mencukupi!\nSisa stok: " + d.getProdukObj().getStok(), 
                    "Stok Habis", JOptionPane.WARNING_MESSAGE);
            } else {
                d.setQty(newQty);
                refreshCartList();
            }
        });

        ctrl.add(btnMinus);
        ctrl.add(lblQty);
        ctrl.add(btnPlus);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(ctrl, BorderLayout.EAST);

        return card;
    }

    @Override
    public void onPageLoad() {
        cbPelanggan.removeAllItems();
        for (Pelanggan c : pelangganService.getAllCustomers()) {
            cbPelanggan.addItem(c);
        }
        performProductSearch();
        resetPanel();
    }

    private void performProductSearch() {
        String query = txtProductSearch.getText().trim();
        List<Produk> list = produkService.searchProducts(query);
        quickProductList.removeAll();
        for (Produk p : list) {
            quickProductList.add(buildQuickProductCard(p));
        }
        quickProductList.revalidate();
        quickProductList.repaint();
    }

    private void addProductToCart(Produk selectedProduk) {
        DetailTransaksi existingDetail = null;
        for (DetailTransaksi d : cartItems) {
            if (d.getProdukId().equals(selectedProduk.getId())) {
                existingDetail = d;
                break;
            }
        }

        int requestedQty = 1;
        if (existingDetail != null) {
            requestedQty = existingDetail.getQty() + 1;
        }

        if (selectedProduk.getStok() < requestedQty) {
            JOptionPane.showMessageDialog(this, 
                "Stok tidak mencukupi!\nSisa stok " + selectedProduk.getNama() + ": " + selectedProduk.getStok(), 
                "Stok Habis", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (existingDetail != null) {
            existingDetail.setQty(requestedQty);
        } else {
            DetailTransaksi detail = new DetailTransaksi(selectedProduk.getId(), 1, selectedProduk.getHarga());
            detail.setProdukObj(selectedProduk);
            cartItems.add(detail);
        }

        refreshCartList();
    }

    private void refreshCartList() {
        cartListContainer.removeAll();
        totalBelanja = 0;

        for (DetailTransaksi d : cartItems) {
            cartListContainer.add(buildCartItemCard(d));
            cartListContainer.add(Box.createRigidArea(new Dimension(0, 10)));
            totalBelanja += d.getSubtotal();
        }

        lblTotalBelanja.setText(rpFormat.format(totalBelanja).replace("Rp", "Rp ").replace(",00", ""));
        calculateChange();

        cartListContainer.revalidate();
        cartListContainer.repaint();
    }

    private void calculateChange() {
        String cashStr = txtCash.getText().replaceAll("[^0-9]", "");
        if (cashStr.isEmpty()) {
            lblChange.setText("Rp 0");
            lblChange.setForeground(COLOR_TEXT_DARK);
            return;
        }

        try {
            double cash = Double.parseDouble(cashStr);
            double change = cash - totalBelanja;
            if (change < 0) {
                lblChange.setText("Kurang: " + rpFormat.format(Math.abs(change)).replace("Rp", "Rp ").replace(",00", ""));
                lblChange.setForeground(COLOR_DANGER_RED);
            } else {
                lblChange.setText(rpFormat.format(change).replace("Rp", "Rp ").replace(",00", ""));
                lblChange.setForeground(COLOR_BTN_PRIMARY);
            }
        } catch (NumberFormatException e) {
            lblChange.setText("Input Tidak Valid");
            lblChange.setForeground(COLOR_DANGER_RED);
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
            int pelangganId = pelanggan != null ? pelanggan.getId() : 1;

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
            transaksiService.processTransaction(trx);

            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            NotaDialog notaDialog = new NotaDialog(parentWindow, trx);
            notaDialog.setVisible(true);

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
        cartListContainer.removeAll();
        totalBelanja = 0;
        lblTotalBelanja.setText("Rp 0");
        txtCash.setText("");
        lblChange.setText("Rp 0");
        lblChange.setForeground(COLOR_TEXT_DARK);
        txtProductSearch.setText("");
        performProductSearch();

        cartListContainer.revalidate();
        cartListContainer.repaint();
    }
}
