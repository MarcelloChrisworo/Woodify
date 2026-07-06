package com.woodify.view;

import com.woodify.config.SessionManager;
import com.woodify.model.User;
import com.woodify.view.dashboard.DashboardPanel;
import com.woodify.view.product.ProdukPanel;
import com.woodify.view.customer.PelangganPanel;
import com.woodify.view.transaction.TransaksiPanel;
import com.woodify.view.report.LaporanPanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final JPanel sidebarPanel;
    private final User currentUser;

    private final Map<String, BasePanel> panels = new HashMap<>();

    // Palette warna
    private static final Color COLOR_PRIMARY = new Color(27, 77, 62);
    private static final Color COLOR_SECONDARY = new Color(212, 163, 115);
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_BG_LIGHT = new Color(248, 249, 250);

    public MainFrame() {
        this.currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            // Pengamanan jika masuk tanpa sesi
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            throw new IllegalStateException("Sesi user kosong. Harus login.");
        }

        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
        this.sidebarPanel = new JPanel();

        initUI();
    }

    private void initUI() {
        setTitle("Woodify - Sistem Informasi Penjualan UMKM Furnitur");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout utama
        setLayout(new BorderLayout());

        // Setup Sidebar & Panel Konten
        setupSidebar();
        setupContentPanels();

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        
        // Load Halaman Pertama (Dashboard)
        showPanel("dashboard");
    }

    private void setupSidebar() {
        sidebarPanel.setPreferredSize(new Dimension(240, 700));
        sidebarPanel.setBackground(COLOR_PRIMARY);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        // Area Profil User
        JLabel lblAvatar = new JLabel("👤");
        lblAvatar.setFont(new Font("SansSerif", Font.PLAIN, 40));
        lblAvatar.setForeground(COLOR_SECONDARY);
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblName = new JLabel(currentUser.getNamaLengkap());
        lblName.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblName.setForeground(COLOR_WHITE);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblRole = new JLabel(currentUser.getRoleDisplay());
        lblRole.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblRole.setForeground(COLOR_SECONDARY);
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebarPanel.add(lblAvatar);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(lblName);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        sidebarPanel.add(lblRole);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        sidebarPanel.add(new JSeparator());
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Navigasi Menu
        addMenuButton("Beranda / Dashboard", "dashboard");
        
        // PBO Access Control: Kasir hanya bisa melihat stok produk, tidak CRUD.
        addMenuButton("Manajemen Produk", "produk");
        addMenuButton("Manajemen Pelanggan", "pelanggan");
        addMenuButton("Transaksi Penjualan", "transaksi");

        // PBO Access Control / Polymorphism: Laporan hanya untuk Owner
        if (currentUser.hasAccessToReports()) {
            addMenuButton("Laporan Penjualan", "laporan");
        }

        // Space filler
        sidebarPanel.add(Box.createVerticalGlue());

        // Tombol Logout
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(COLOR_WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setMaximumSize(new Dimension(210, 35));
        btnLogout.setPreferredSize(new Dimension(210, 35));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> handleLogout());
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        sidebarPanel.add(btnLogout);
    }

    private void addMenuButton(String text, String targetPanelName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(COLOR_WHITE);
        btn.setBackground(COLOR_PRIMARY);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setMaximumSize(new Dimension(210, 40));
        btn.setPreferredSize(new Dimension(210, 40));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.addActionListener(e -> showPanel(targetPanelName));

        sidebarPanel.add(btn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void setupContentPanels() {
        // Daftarkan panel-panel modular ke Map & CardLayout
        panels.put("dashboard", new DashboardPanel());
        panels.put("produk", new ProdukPanel());
        panels.put("pelanggan", new PelangganPanel());
        panels.put("transaksi", new TransaksiPanel());
        
        if (currentUser.hasAccessToReports()) {
            panels.put("laporan", new LaporanPanel());
        }

        // Masukkan panel ke container CardLayout
        for (Map.Entry<String, BasePanel> entry : panels.entrySet()) {
            contentPanel.add(entry.getValue(), entry.getKey());
        }
    }

    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
        
        // Panggil reload database untuk panel yang aktif
        BasePanel activePanel = panels.get(panelName);
        if (activePanel != null) {
            activePanel.onPageLoad();
        }
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin logout?", "Konfirmasi Logout", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}
