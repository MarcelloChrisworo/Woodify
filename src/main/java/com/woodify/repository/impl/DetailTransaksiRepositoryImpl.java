package com.woodify.repository.impl;

import com.woodify.config.DatabaseConnection;
import com.woodify.model.DetailTransaksi;
import com.woodify.repository.DetailTransaksiRepository;
import com.woodify.exception.DatabaseConnectionException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetailTransaksiRepositoryImpl implements DetailTransaksiRepository {

    @Override
    public void save(DetailTransaksi detail) {
        String sql = "INSERT INTO detail_transaksi (transaksi_id, produk_id, qty, harga_jual, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, detail.getTransaksiId());
            pstmt.setString(2, detail.getProdukId());
            pstmt.setInt(3, detail.getQty());
            pstmt.setDouble(4, detail.getHargaJual());
            pstmt.setDouble(5, detail.getSubtotal());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    detail.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseConnectionException("Gagal menyimpan detail transaksi: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DetailTransaksi> findByTransaksiId(String transaksiId) {
        List<DetailTransaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM detail_transaksi WHERE transaksi_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, transaksiId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DetailTransaksi detail = new DetailTransaksi(
                            rs.getInt("id"),
                            rs.getString("transaksi_id"),
                            rs.getString("produk_id"),
                            rs.getInt("qty"),
                            rs.getDouble("harga_jual"),
                            rs.getDouble("subtotal")
                    );
                    list.add(detail);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal memuat detail transaksi.", e);
        }
        return list;
    }
}
