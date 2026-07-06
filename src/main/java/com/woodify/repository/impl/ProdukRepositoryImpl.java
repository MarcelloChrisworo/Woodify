package com.woodify.repository.impl;

import com.woodify.config.DatabaseConnection;
import com.woodify.model.Produk;
import com.woodify.repository.ProdukRepository;
import com.woodify.exception.DatabaseConnectionException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdukRepositoryImpl implements ProdukRepository {

    private Produk mapRowToProduk(ResultSet rs) throws SQLException {
        return new Produk(
                rs.getString("id"),
                rs.getString("nama"),
                rs.getString("kategori"),
                rs.getDouble("harga"),
                rs.getInt("stok"),
                rs.getString("deskripsi")
        );
    }

    @Override
    public void save(Produk entity) {
        String sql = "INSERT INTO produk (id, nama, kategori, harga, stok, deskripsi) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entity.getId());
            pstmt.setString(2, entity.getNama());
            pstmt.setString(3, entity.getKategori());
            pstmt.setDouble(4, entity.getHarga());
            pstmt.setInt(5, entity.getStok());
            pstmt.setString(6, entity.getDeskripsi());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal menyimpan produk baru.", e);
        }
    }

    @Override
    public void update(Produk entity) {
        String sql = "UPDATE produk SET nama = ?, kategori = ?, harga = ?, stok = ?, deskripsi = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entity.getNama());
            pstmt.setString(2, entity.getKategori());
            pstmt.setDouble(3, entity.getHarga());
            pstmt.setInt(4, entity.getStok());
            pstmt.setString(5, entity.getDeskripsi());
            pstmt.setString(6, entity.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal mengubah produk.", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM produk WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal menghapus produk.", e);
        }
    }

    @Override
    public Produk findById(String id) {
        String sql = "SELECT * FROM produk WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToProduk(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal mencari produk berdasarkan id.", e);
        }
        return null;
    }

    @Override
    public List<Produk> findAll() {
        List<Produk> list = new ArrayList<>();
        String sql = "SELECT * FROM produk ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToProduk(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal memuat daftar produk.", e);
        }
        return list;
    }

    @Override
    public List<Produk> searchByNamaOrId(String keyword) {
        List<Produk> list = new ArrayList<>();
        String sql = "SELECT * FROM produk WHERE id LIKE ? OR nama LIKE ? OR kategori LIKE ? ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword + "%";
            pstmt.setString(1, likeKeyword);
            pstmt.setString(2, likeKeyword);
            pstmt.setString(3, likeKeyword);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToProduk(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal melakukan pencarian produk.", e);
        }
        return list;
    }

    @Override
    public List<Produk> findCriticalStock(int limitStock) {
        List<Produk> list = new ArrayList<>();
        String sql = "SELECT * FROM produk WHERE stok <= ? ORDER BY stok ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limitStock);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToProduk(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal mencari produk dengan stok kritis.", e);
        }
        return list;
    }
}
