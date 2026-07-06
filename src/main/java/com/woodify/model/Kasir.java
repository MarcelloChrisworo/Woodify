package com.woodify.model;

public class Kasir extends User {
    
    public Kasir(int id, String username, String password, String namaLengkap) {
        super(id, username, password, namaLengkap, "KASIR");
    }

    // Overriding: Implementasi khusus Kasir
    @Override
    public String getRoleDisplay() {
        return "Kasir Toko Woodify";
    }

    @Override
    public boolean hasAccessToReports() {
        return false; // Kasir tidak bisa mengakses laporan penjualan keuangan detail
    }

    @Override
    public boolean hasAccessToProductManagement() {
        return false; // Kasir hanya bisa melihat stok, tidak mengelola produk
    }
}
