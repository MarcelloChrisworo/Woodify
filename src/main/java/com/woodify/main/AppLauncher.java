package com.woodify.main;

import com.woodify.config.DatabaseConnection;
import com.woodify.view.LoginFrame;

import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        // Set Look and Feel system
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Gagal mengatur Look and Feel: " + e.getMessage());
        }

        // Jalankan koneksi database awal untuk inisialisasi schema (SQLite)
        try {
            DatabaseConnection.getConnection();
        } catch (Exception e) {
            System.err.println("Gagal inisialisasi database: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Gagal inisialisasi database. Detail: " + e.getMessage(), 
                "Error Inisialisasi", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Tampilkan LoginFrame di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
