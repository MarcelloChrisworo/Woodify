package com.woodify.repository.impl;

import com.woodify.config.DatabaseConnection;
import com.woodify.model.Transaksi;
import com.woodify.repository.TransaksiRepository;
import com.woodify.exception.DatabaseConnectionException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class TransaksiRepositoryImpl implements TransaksiRepository {

    private Transaksi mapRowToTransaksi(ResultSet rs) throws SQLException {
        return new Transaksi(
                rs.getString("id"),
                rs.getInt("user_id"),
                rs.getInt("pelanggan_id"),
                new Date(rs.getTimestamp("tanggal").getTime()),
                rs.getDouble("total_harga"),
                rs.getDouble("bayar"),
                rs.getDouble("kembalian")
        );
    }

    @Override
    public void save(Transaksi entity) {
        String sql = "INSERT INTO transaksi (id, user_id, pelanggan_id, tanggal, total_harga, bayar, kembalian) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entity.getId());
            pstmt.setInt(2, entity.getUserId());
            
            if (entity.getPelangganId() > 0) {
                pstmt.setInt(3, entity.getPelangganId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            
            pstmt.setTimestamp(4, new Timestamp(entity.getTanggal().getTime()));
            pstmt.setDouble(5, entity.getTotalHarga());
            pstmt.setDouble(6, entity.getBayar());
            pstmt.setDouble(7, entity.getKembalian());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal menyimpan transaksi.", e);
        }
    }

    @Override
    public void update(Transaksi entity) {
        String sql = "UPDATE transaksi SET user_id = ?, pelanggan_id = ?, tanggal = ?, total_harga = ?, bayar = ?, kembalian = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, entity.getUserId());
            
            if (entity.getPelangganId() > 0) {
                pstmt.setInt(2, entity.getPelangganId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            
            pstmt.setTimestamp(3, new Timestamp(entity.getTanggal().getTime()));
            pstmt.setDouble(4, entity.getTotalHarga());
            pstmt.setDouble(5, entity.getBayar());
            pstmt.setDouble(6, entity.getKembalian());
            pstmt.setString(7, entity.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal mengubah transaksi.", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM transaksi WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal menghapus transaksi.", e);
        }
    }

    @Override
    public Transaksi findById(String id) {
        String sql = "SELECT * FROM transaksi WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTransaksi(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal mencari transaksi berdasarkan id.", e);
        }
        return null;
    }

    @Override
    public List<Transaksi> findAll() {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi ORDER BY tanggal DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToTransaksi(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal memuat daftar riwayat transaksi.", e);
        }
        return list;
    }

    @Override
    public List<Transaksi> findByDateRange(Date start, Date end) {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi WHERE tanggal BETWEEN ? AND ? ORDER BY tanggal DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, new Timestamp(start.getTime()));
            pstmt.setTimestamp(2, new Timestamp(end.getTime()));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToTransaksi(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal memuat daftar transaksi berdasarkan rentang tanggal.", e);
        }
        return list;
    }
}
