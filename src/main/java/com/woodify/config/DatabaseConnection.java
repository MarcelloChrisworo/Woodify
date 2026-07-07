package com.woodify.config;

import com.woodify.exception.DatabaseConnectionException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String MYSQL_HOST = "localhost";
    private static final String MYSQL_PORT = "3306";
    private static final String MYSQL_DB = "woodify_db";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = ""; // Kosong jika menggunakan default XAMPP
    
    private static final String MYSQL_URL = "jdbc:mysql://" + MYSQL_HOST + ":" + MYSQL_PORT + "/" + MYSQL_DB;
    
    // SQLite Fallback Config
    private static final String SQLITE_URL = "jdbc:sqlite:woodify.db";
    
    // Gunakan boolean untuk mempermudah perpindahan antar engine database
    private static final boolean USE_SQLITE = true; 

    private static Connection rawConnection = null;
    private static Connection connection = null;

    private DatabaseConnection() {
        // Private constructor to prevent instantiation
    }

    public static synchronized Connection getConnection() {
        try {
            if (rawConnection == null || rawConnection.isClosed()) {
                if (USE_SQLITE) {
                    Class.forName("org.sqlite.JDBC");
                    rawConnection = DriverManager.getConnection(SQLITE_URL);
                    try (Statement pragmaStmt = rawConnection.createStatement()) {
                        pragmaStmt.execute("PRAGMA foreign_keys = ON;");
                    }
                    System.out.println("Koneksi database SQLite berhasil.");
                } else {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    
                    // Sebelum koneksi ke database spesifik, pastikan database woodify_db ada
                    try {
                        String baseUrl = "jdbc:mysql://" + MYSQL_HOST + ":" + MYSQL_PORT;
                        try (Connection baseConn = DriverManager.getConnection(baseUrl, MYSQL_USER, MYSQL_PASSWORD);
                             Statement stmt = baseConn.createStatement()) {
                            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + MYSQL_DB);
                        }
                    } catch (SQLException e) {
                        System.err.println("Gagal memastikan keberadaan database: " + e.getMessage());
                    }

                    rawConnection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
                    System.out.println("Koneksi database MySQL berhasil.");
                }
                
                // Secara otomatis buat tabel jika belum ada
                initializeDatabaseSchema(rawConnection);

                // Create a proxy that ignores close() calls to preserve transaction connection singleton
                final Connection finalRaw = rawConnection;
                connection = (Connection) java.lang.reflect.Proxy.newProxyInstance(
                    DatabaseConnection.class.getClassLoader(),
                    new Class[]{Connection.class},
                    (proxy, method, args) -> {
                        if ("close".equals(method.getName())) {
                            // Ignore close() to keep singleton connection open during try-with-resources
                            return null;
                        }
                        try {
                            return method.invoke(finalRaw, args);
                        } catch (java.lang.reflect.InvocationTargetException ite) {
                            throw ite.getCause();
                        }
                    }
                );
            }
        } catch (ClassNotFoundException e) {
            throw new DatabaseConnectionException("Driver database tidak ditemukan.", e);
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Gagal melakukan koneksi ke database. Pastikan MySQL aktif.", e);
        }
        return connection;
    }

    public static void closeConnection() {
        if (rawConnection != null) {
            try {
                if (!rawConnection.isClosed()) {
                    rawConnection.close();
                    System.out.println("Koneksi database ditutup.");
                }
            } catch (SQLException e) {
                System.err.println("Gagal menutup koneksi database: " + e.getMessage());
            } finally {
                rawConnection = null;
                connection = null;
            }
        }
    }

    private static void initializeDatabaseSchema(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            String pkSyntax = USE_SQLITE ? "INTEGER PRIMARY KEY AUTOINCREMENT" : "INT AUTO_INCREMENT PRIMARY KEY";

            // 1. Tabel users
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id " + pkSyntax + "," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "password VARCHAR(255) NOT NULL," +
                    "nama_lengkap VARCHAR(100) NOT NULL," +
                    "role VARCHAR(20) NOT NULL" +
                    ")");

            // 2. Tabel produk
            stmt.execute("CREATE TABLE IF NOT EXISTS produk (" +
                    "id VARCHAR(20) PRIMARY KEY," +
                    "nama VARCHAR(100) NOT NULL," +
                    "kategori VARCHAR(50) NOT NULL," +
                    "harga DOUBLE NOT NULL," +
                    "stok INT NOT NULL," +
                    "deskripsi TEXT" +
                    ")");

            // 3. Tabel pelanggan
            stmt.execute("CREATE TABLE IF NOT EXISTS pelanggan (" +
                    "id " + pkSyntax + "," +
                    "nama VARCHAR(100) NOT NULL," +
                    "telepon VARCHAR(20)," +
                    "alamat TEXT" +
                    ")");

            // 4. Tabel transaksi
            stmt.execute("CREATE TABLE IF NOT EXISTS transaksi (" +
                    "id VARCHAR(50) PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "pelanggan_id INT," +
                    "tanggal DATETIME NOT NULL," +
                    "total_harga DOUBLE NOT NULL," +
                    "bayar DOUBLE NOT NULL," +
                    "kembalian DOUBLE NOT NULL," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)," +
                    "FOREIGN KEY (pelanggan_id) REFERENCES pelanggan(id) ON DELETE SET NULL" +
                    ")");

            // 5. Tabel detail_transaksi
            stmt.execute("CREATE TABLE IF NOT EXISTS detail_transaksi (" +
                    "id " + pkSyntax + "," +
                    "transaksi_id VARCHAR(50) NOT NULL," +
                    "produk_id VARCHAR(20) NOT NULL," +
                    "qty INT NOT NULL," +
                    "harga_jual DOUBLE NOT NULL," +
                    "subtotal DOUBLE NOT NULL," +
                    "FOREIGN KEY (transaksi_id) REFERENCES transaksi(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (produk_id) REFERENCES produk(id)" +
                    ")");
            
            // Tambahkan user dummy jika tabel users kosong
            insertDummyDataIfNeeded(conn);
        }
    }

    private static void insertDummyDataIfNeeded(Connection conn) throws SQLException {
        // Cek jika user kosong
        try (Statement checkStmt = conn.createStatement();
             var rs = checkStmt.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) == 0) {
                // Insert default Owner (password: admin123) dan Kasir (password: kasir123)
                try (Statement insertStmt = conn.createStatement()) {
                    insertStmt.execute("INSERT INTO users (username, password, nama_lengkap, role) VALUES " +
                            "('admin', 'admin123', 'Pak Budi (Owner)', 'OWNER')," +
                            "('kasir', 'kasir123', 'Sari Dewi', 'KASIR')");
                    
                    // Insert default Pelanggan Umum
                    insertStmt.execute("INSERT INTO pelanggan (nama, telepon, alamat) VALUES " +
                            "('Pelanggan Umum', '-', '-')");

                    // Insert default Produk
                    insertStmt.execute("INSERT INTO produk (id, nama, kategori, harga, stok, deskripsi) VALUES " +
                            "('PRD-001', 'Sofa Jati Minimalis', 'Kursi & Sofa', 4500000, 10, 'Sofa kayu jati berkualitas tinggi dengan busa empuk.')," +
                            "('PRD-002', 'Meja Makan Mahoni', 'Meja', 2500000, 5, 'Meja makan elegan berkapasitas 4 orang berbahan kayu mahoni.')," +
                            "('PRD-003', 'Lemari Pakaian Slide', 'Lemari', 3500000, 3, 'Lemari pakaian 2 pintu geser dengan cermin besar.')," +
                            "('PRD-004', 'Rak Buku Gantung', 'Dekorasi', 350000, 15, 'Rak buku minimalis gantung untuk mempercantik ruangan.')");
                    
                    System.out.println("Data dummy berhasil diinisialisasi.");
                }
            }
        }
    }
}
