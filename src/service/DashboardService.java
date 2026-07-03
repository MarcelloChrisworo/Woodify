package com.woodify.service;

import java.util.Map;

/**
 * Abstraction (Interface): Kontrak untuk layanan data dashboard.
 * Menyediakan data ringkasan yang ditampilkan di halaman Dashboard.
 */
public interface DashboardService {
    /**
     * Mengambil statistik ringkasan untuk ditampilkan di dashboard.
     * Key yang dikembalikan:
     * - "totalProduk"       : int
     * - "totalPelanggan"    : int
     * - "totalTransaksi"    : int
     * - "omzetBulanIni"     : double
     * - "transaksiHariIni"  : int
     */
    Map<String, Object> getDashboardStats();
}
