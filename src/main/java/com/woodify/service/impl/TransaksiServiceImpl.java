package com.woodify.service.impl;

import com.woodify.config.DatabaseConnection;
import com.woodify.exception.DatabaseConnectionException;
import com.woodify.exception.InsufficientStockException;
import com.woodify.exception.ValidationException;
import com.woodify.model.DetailTransaksi;
import com.woodify.model.Produk;
import com.woodify.model.Transaksi;
import com.woodify.repository.DetailTransaksiRepository;
import com.woodify.repository.ProdukRepository;
import com.woodify.repository.TransaksiRepository;
import com.woodify.repository.impl.DetailTransaksiRepositoryImpl;
import com.woodify.repository.impl.ProdukRepositoryImpl;
import com.woodify.repository.impl.TransaksiRepositoryImpl;
import com.woodify.service.TransaksiService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TransaksiServiceImpl implements TransaksiService {
    private final TransaksiRepository transaksiRepository;
    private final DetailTransaksiRepository detailTransaksiRepository;
    private final ProdukRepository produkRepository;

    public TransaksiServiceImpl() {
        this.transaksiRepository = new TransaksiRepositoryImpl();
        this.detailTransaksiRepository = new DetailTransaksiRepositoryImpl();
        this.produkRepository = new ProdukRepositoryImpl();
    }

    @Override
    public void processTransaction(Transaksi transaksi) {
        if (transaksi.getDetails().isEmpty()) {
            throw new ValidationException("Keranjang belanja kosong. Transaksi tidak dapat diproses.");
        }
        
        Connection conn = DatabaseConnection.getConnection();
        boolean originalAutoCommit = true;
        
        try {
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false); // Memulai SQL Transaction

            // 1. Simpan Transaksi Utama
            transaksiRepository.save(transaksi);

            // 2. Loop detail item
            for (DetailTransaksi detail : transaksi.getDetails()) {
                Produk produk = produkRepository.findById(detail.getProdukId());
                if (produk == null) {
                    throw new ValidationException("Produk dengan ID '" + detail.getProdukId() + "' tidak ditemukan.");
                }

                // Cek stok (PBO Custom Exception)
                if (produk.getStok() < detail.getQty()) {
                    throw new InsufficientStockException(produk.getNama(), produk.getStok(), detail.getQty());
                }

                // Potong stok produk
                produk.setStok(produk.getStok() - detail.getQty());
                produkRepository.update(produk);

                // Set ID transaksi di detail dan simpan
                detail.setTransaksiId(transaksi.getId());
                detailTransaksiRepository.save(detail);
            }

            conn.commit(); // Commit jika semua sukses
            System.out.println("Transaksi commit berhasil: " + transaksi.getId());
        } catch (Exception e) {
            try {
                conn.rollback(); // Rollback jika ada kegagalan
                System.err.println("Transaksi di-rollback karena kesalahan: " + e.getMessage());
            } catch (SQLException ex) {
                System.err.println("Gagal melakukan rollback: " + ex.getMessage());
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new DatabaseConnectionException("Gagal memproses transaksi.", e);
        } finally {
            try {
                conn.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                System.err.println("Gagal mengembalikan autocommit: " + e.getMessage());
            }
        }
    }

    @Override
    public Transaksi getTransactionById(String id) {
        Transaksi t = transaksiRepository.findById(id);
        if (t != null) {
            // Load detail list
            List<DetailTransaksi> details = detailTransaksiRepository.findByTransaksiId(id);
            for (DetailTransaksi detail : details) {
                detail.setProdukObj(produkRepository.findById(detail.getProdukId()));
            }
            t.setDetails(details);
        }
        return t;
    }

    @Override
    public List<Transaksi> getAllTransactions() {
        return transaksiRepository.findAll();
    }

    @Override
    public List<Transaksi> getTransactionsByDateRange(Date start, Date end) {
        return transaksiRepository.findByDateRange(start, end);
    }

    @Override
    public String generateNewTransactionId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String prefix = "TRX-" + sdf.format(new Date()) + "-";
        
        // Cari id transaksi terakhir yang diawali prefix tersebut untuk auto-increment
        int count = 0;
        String sql = "SELECT COUNT(*) FROM transaksi WHERE id LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, prefix + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal generate ID transaksi: " + e.getMessage());
        }
        
        return prefix + String.format("%04d", count + 1);
    }
}
