package com.woodify.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility class untuk manipulasi tanggal dan waktu.
 */
public final class DateUtil {

    public static final String FORMAT_DISPLAY    = "dd-MM-yyyy HH:mm:ss";
    public static final String FORMAT_DATE_ONLY  = "dd-MM-yyyy";
    public static final String FORMAT_ID_SUFFIX  = "yyyyMMdd";

    // Prevent instantiation
    private DateUtil() {}

    /** Memformat tanggal ke format tampilan standar Woodify. */
    public static String format(Date date) {
        if (date == null) return "-";
        return new SimpleDateFormat(FORMAT_DISPLAY).format(date);
    }

    /** Memformat tanggal ke format date-only (tanpa jam). */
    public static String formatDateOnly(Date date) {
        if (date == null) return "-";
        return new SimpleDateFormat(FORMAT_DATE_ONLY).format(date);
    }

    /** Mengembalikan Date pada awal hari (00:00:00) dari tanggal input. */
    public static Date startOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /** Mengembalikan Date pada akhir hari (23:59:59) dari tanggal input. */
    public static Date endOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /** Mengembalikan Date pada awal bulan ini. */
    public static Date startOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return startOfDay(cal.getTime());
    }

    /** Suffix tanggal untuk ID transaksi, contoh: "20260703". */
    public static String todaySuffix() {
        return new SimpleDateFormat(FORMAT_ID_SUFFIX).format(new Date());
    }
}
