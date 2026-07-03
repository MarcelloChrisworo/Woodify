package com.woodify.view;

import com.woodify.config.SessionManager;
import com.woodify.exception.AuthenticationException;
import com.woodify.model.User;
import com.woodify.service.AuthService;
import com.woodify.service.impl.AuthServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private final AuthService authService;
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblError;

    // Palet warna yang sama dengan BasePanel untuk keselarasan
    private static final Color COLOR_PRIMARY = new Color(27, 77, 62);
    private static final Color COLOR_SECONDARY = new Color(212, 163, 115);
    private static final Color COLOR_BG = new Color(248, 249, 250);

    public LoginFrame() {
        this.authService = new AuthServiceImpl();
        initUI();
    }

    private void initUI() {
        setTitle("Woodify - Login Penjualan");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel Utama dengan Background
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Logo / Title Panel
        JLabel lblLogo = new JLabel("W O O D I F Y");
        lblLogo.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblLogo.setForeground(COLOR_PRIMARY);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Sistem Informasi Penjualan UMKM");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Spasi
        mainPanel.add(lblLogo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(lblSub);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Form Username
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblUsername.setForeground(COLOR_PRIMARY);
        lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtUsername = new JTextField();
        txtUsername.setMaximumSize(new Dimension(320, 35));
        txtUsername.setPreferredSize(new Dimension(320, 35));
        txtUsername.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        mainPanel.add(lblUsername);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(txtUsername);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Form Password
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblPassword.setForeground(COLOR_PRIMARY);
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(new Dimension(320, 35));
        txtPassword.setPreferredSize(new Dimension(320, 35));
        txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        mainPanel.add(lblPassword);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(txtPassword);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Label Error
        lblError = new JLabel(" ");
        lblError.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblError.setForeground(new Color(220, 53, 69));
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblError);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Tombol Login
        btnLogin = new JButton("LOGIN");
        btnLogin.setMaximumSize(new Dimension(320, 40));
        btnLogin.setPreferredSize(new Dimension(320, 40));
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnLogin.setBackground(COLOR_PRIMARY);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBorder(BorderFactory.createEmptyBorder());
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Action Listener
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        // Enter key listener on password field
        txtPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        mainPanel.add(btnLogin);
        add(mainPanel);
    }

    private void handleLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        try {
            lblError.setText(" ");
            User loggedInUser = authService.authenticate(username, password);
            
            // Tutup LoginFrame, Buka MainFrame
            dispose();
            
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            });
        } catch (AuthenticationException ex) {
            lblError.setText(ex.getMessage());
        } catch (Exception ex) {
            lblError.setText("Terjadi kesalahan sistem: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
