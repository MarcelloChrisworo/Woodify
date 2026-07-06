package com.woodify.service.impl;

import com.woodify.config.DatabaseConnection;
import com.woodify.exception.DatabaseConnectionException;
import com.woodify.repository.PelangganRepository;
import com.woodify.repository.ProdukRepository;
import com.woodify.repository.TransaksiRepository;
import com.woodify.repository.impl.PelangganRepositoryImpl;
import com.woodify.repository.impl.ProdukRepositoryImpl;
import com.woodify.repository.impl.TransaksiRepositoryImpl;
import com.woodify.service.DashboardService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation dari DashboardService.
 * Mengambil statistik aggregat dari database untuk keperluan Dashboard.
 * Sequence: DashboardPanel -> DashboardServiceImpl -> Repository/DB -> kembali ke panel
 */
public class DashboardServiceImpl implements DashboardService {

    private final ProdukRepository produkRepository;
    private final PelangganRepository pelangganRepository;
    private final TransaksiRepository transaksiRepository;

    public DashboardServiceImpl() {
        this.produkRepository   = new ProdukRepositoryImpl();
        this.pelangganRepository = new PelangganRepositoryImpl();
        this.transaksiRepository = new TransaksiRepositoryImpl();
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Rentang waktu
        Calendar cal = Calendar.getInstance();
        // Awal hari ini
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startOfToday = cal.getTime();

        // Awal bulan ini
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startOfMonth = cal.getTime();

        // Sekarang
        Date now = new Date();

        try {
            Connection conn = DatabaseConnection.getConnection();

            // 1. Total Produk
            stats.put("totalProduk", countFromTable(conn, "SELECT COUNT(*) FROM produk"));

            // 2. Total Pelanggan
            stats.put("totalPelanggan", countFromTable(conn, "SELECT COUNT(*) FROM pelanggan"));

            // 3. Total Transaksi (all time)
            stats.put("totalTransaksi", countFromTable(conn, "SELECT COUNT(*) FROM transaksi"));

            // 4. Omzet Bulan Ini
            stats.put("omzetBulanIni", sumFromTable(conn,
                    "SELECT COALESCE(SUM(total_harga), 0) FROM transaksi WHERE tanggal BETWEEN ? AND ?",
                    new Timestamp(startOfMonth.getTime()), new Timestamp(now.getTime())));

            // 5. Jumlah Transaksi Hari Ini
            stats.put("transaksiHariIni", countFromTableRange(conn,
                    "SELECT COUNT(*) FROM transaksi WHERE tanggal BETWEEN ? AND ?",
                    new Timestamp(startOfToday.getTime()), new Timestamp(now.getTime())));

        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal mengambil data dashboard.", e);
        }

        return stats;
    }

    // ─── Private Helpers ────────────────────────────────────────────────────

    private int countFromTable(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int countFromTableRange(Connection conn, String sql, Timestamp start, Timestamp end) throws SQLException {
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, start);
            pstmt.setTimestamp(2, end);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private double sumFromTable(Connection conn, String sql, Timestamp start, Timestamp end) throws SQLException {
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, start);
            pstmt.setTimestamp(2, end);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        }
    }
}
