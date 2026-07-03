package com.woodify.util;

import com.woodify.exception.ValidationException;

/**
 * Utility class untuk validasi input form.
 * Melempar ValidationException jika validasi gagal.
 */
public final class ValidationUtil {

    // Prevent instantiation
    private ValidationUtil() {}

    /**
     * Memastikan string tidak null dan tidak kosong.
     * @throws ValidationException jika field kosong
     */
    public static void requireNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("Field '" + fieldName + "' tidak boleh kosong.");
        }
    }

    /**
     * Memastikan nilai numerik lebih besar dari nol.
     * @throws ValidationException jika nilai <= 0
     */
    public static void requirePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new ValidationException("Field '" + fieldName + "' harus lebih dari 0.");
        }
    }

    /**
     * Memastikan nilai integer tidak negatif.
     * @throws ValidationException jika nilai < 0
     */
    public static void requireNonNegativeInt(int value, String fieldName) {
        if (value < 0) {
            throw new ValidationException("Field '" + fieldName + "' tidak boleh negatif.");
        }
    }

    /**
     * Mem-parse string ke double, melempar ValidationException jika tidak valid.
     */
    public static double parseDouble(String text, String fieldName) {
        requireNonEmpty(text, fieldName);
        try {
            return Double.parseDouble(text.trim().replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            throw new ValidationException("Field '" + fieldName + "' harus berupa angka valid.");
        }
    }

    /**
     * Mem-parse string ke int, melempar ValidationException jika tidak valid.
     */
    public static int parseInt(String text, String fieldName) {
        requireNonEmpty(text, fieldName);
        try {
            return Integer.parseInt(text.trim().replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            throw new ValidationException("Field '" + fieldName + "' harus berupa bilangan bulat valid.");
        }
    }
}
