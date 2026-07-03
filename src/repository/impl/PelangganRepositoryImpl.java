package com.woodify.repository.impl;

import com.woodify.config.DatabaseConnection;
import com.woodify.model.Pelanggan;
import com.woodify.repository.PelangganRepository;
import com.woodify.exception.DatabaseConnectionException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PelangganRepositoryImpl implements PelangganRepository {

    private Pelanggan mapRowToPelanggan(ResultSet rs) throws SQLException {
        return new Pelanggan(
                rs.getInt("id"),
                rs.getString("nama"),
                rs.getString("telepon"),
                rs.getString("alamat")
        );
    }

    @Override
    public void save(Pelanggan entity) {
        String sql = "INSERT INTO pelanggan (nama, telepon, alamat) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, entity.getNama());
            pstmt.setString(2, entity.getTelepon());
            pstmt.setString(3, entity.getAlamat());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal menyimpan data pelanggan.", e);
        }
    }

    @Override
    public void update(Pelanggan entity) {
        String sql = "UPDATE pelanggan SET nama = ?, telepon = ?, alamat = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entity.getNama());
            pstmt.setString(2, entity.getTelepon());
            pstmt.setString(3, entity.getAlamat());
            pstmt.setInt(4, entity.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal mengubah data pelanggan.", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM pelanggan WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal menghapus data pelanggan.", e);
        }
    }

    @Override
    public Pelanggan findById(Integer id) {
        String sql = "SELECT * FROM pelanggan WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPelanggan(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal mencari pelanggan berdasarkan id.", e);
        }
        return null;
    }

    @Override
    public List<Pelanggan> findAll() {
        List<Pelanggan> list = new ArrayList<>();
        String sql = "SELECT * FROM pelanggan ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToPelanggan(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal memuat daftar pelanggan.", e);
        }
        return list;
    }

    @Override
    public List<Pelanggan> searchByNamaOrTelepon(String keyword) {
        List<Pelanggan> list = new ArrayList<>();
        String sql = "SELECT * FROM pelanggan WHERE nama LIKE ? OR telepon LIKE ? OR alamat LIKE ? ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword + "%";
            pstmt.setString(1, likeKeyword);
            pstmt.setString(2, likeKeyword);
            pstmt.setString(3, likeKeyword);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToPelanggan(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal melakukan pencarian data pelanggan.", e);
        }
        return list;
    }
}
