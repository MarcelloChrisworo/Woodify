package com.woodify.util;

import javax.swing.*;
import java.awt.*;

/**
 * Utility class untuk menampilkan dialog standar Woodify.
 * Menyederhanakan pembuatan JOptionPane berulang di seluruh panel.
 */
public final class DialogUtil {

    // Prevent instantiation
    private DialogUtil() {}

    /** Dialog informasi (biru). */
    public static void showInfo(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /** Dialog sukses (gunakan icon informasi, pesan positif). */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Berhasil ✓", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Dialog peringatan (kuning). */
    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    /** Dialog error (merah). */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Dialog konfirmasi Ya/Tidak.
     * @return true jika user memilih "Ya"
     */
    public static boolean confirm(Component parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(
                parent, message, title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Dialog input teks sederhana.
     * @return string yang diinput user, atau null jika dibatalkan
     */
    public static String promptInput(Component parent, String message, String title) {
        return JOptionPane.showInputDialog(parent, message, title, JOptionPane.PLAIN_MESSAGE);
    }
}
