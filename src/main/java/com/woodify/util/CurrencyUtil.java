package com.woodify.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility class untuk formatting nilai mata uang Rupiah.
 * Semua field static, kelas tidak perlu di-instantiate.
 */
public final class CurrencyUtil {

    private static final NumberFormat RP_FORMAT =
            NumberFormat.getCurrencyInstance(new Locale("in", "ID"));

    // Prevent instantiation
    private CurrencyUtil() {}

    /**
     * Memformat angka ke format Rupiah Indonesia.
     * Contoh: 4500000 → "Rp 4.500.000"
     */
    public static String formatRupiah(double amount) {
        String raw = RP_FORMAT.format(amount);
        // Bersihkan format default Java: "Rp" + spasi, hilangkan ",00"
        return raw.replace("Rp", "Rp ").replace(",00", "");
    }

    /**
     * Mem-parse string angka (bisa mengandung non-digit) ke double.
     * Contoh: "1.700.000" → 1700000.0
     */
    public static double parseAmount(String text) {
        if (text == null || text.trim().isEmpty()) return 0.0;
        String cleaned = text.replaceAll("[^0-9]", "");
        if (cleaned.isEmpty()) return 0.0;
        return Double.parseDouble(cleaned);
    }
}
