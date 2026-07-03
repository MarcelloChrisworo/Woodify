package com.woodify.repository.impl;

import com.woodify.config.DatabaseConnection;
import com.woodify.model.User;
import com.woodify.model.Owner;
import com.woodify.model.Kasir;
import com.woodify.repository.UserRepository;
import com.woodify.exception.DatabaseConnectionException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {

    private User mapRowToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String namaLengkap = rs.getString("nama_lengkap");
        String role = rs.getString("role");

        if ("OWNER".equalsIgnoreCase(role)) {
            return new Owner(id, username, password, namaLengkap);
        } else {
            return new Kasir(id, username, password, namaLengkap);
        }
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal mencari user berdasarkan username.", e);
        }
        return null;
    }

    @Override
    public void save(User entity) {
        String sql = "INSERT INTO users (username, password, nama_lengkap, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, entity.getUsername());
            pstmt.setString(2, entity.getPassword());
            pstmt.setString(3, entity.getNamaLengkap());
            pstmt.setString(4, entity.getRole());
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal menyimpan user baru.", e);
        }
    }

    @Override
    public void update(User entity) {
        String sql = "UPDATE users SET username = ?, password = ?, nama_lengkap = ?, role = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entity.getUsername());
            pstmt.setString(2, entity.getPassword());
            pstmt.setString(3, entity.getNamaLengkap());
            pstmt.setString(4, entity.getRole());
            pstmt.setInt(5, entity.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal mengubah user.", e);
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal menghapus user.", e);
        }
    }

    @Override
    public User findById(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal mencari user berdasarkan id.", e);
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal memuat daftar user.", e);
        }
        return list;
    }
}
