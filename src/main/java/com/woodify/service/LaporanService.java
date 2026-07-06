package com.woodify.service;

import com.woodify.model.Transaksi;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Abstraction (Interface): Kontrak untuk layanan laporan penjualan.
 * Memisahkan kontrak dari implementasi (LaporanServiceImpl).
 */
public interface LaporanService {
    /**
     * Menghasilkan laporan ringkasan dalam rentang tanggal tertentu.
     * Key yang dikembalikan: totalPendapatan, jumlahTransaksi, totalItemTerjual
     */
    Map<String, Object> generateSummaryReport(Date startDate, Date endDate);

    /**
     * Menghasilkan daftar transaksi detail dalam rentang tanggal tertentu.
     */
    List<Transaksi> generateDetailedReport(Date startDate, Date endDate);
}
