package com.woodify.model;

public class Owner extends User {
    
    public Owner(int id, String username, String password, String namaLengkap) {
        super(id, username, password, namaLengkap, "OWNER");
    }

    // Overriding: Implementasi khusus Owner
    @Override
    public String getRoleDisplay() {
        return "Pemilik (Owner) Woodify";
    }

    @Override
    public boolean hasAccessToReports() {
        return true; // Owner bisa mengakses laporan penjualan
    }

    @Override
    public boolean hasAccessToProductManagement() {
        return true; // Owner bisa mengelola produk
    }
}
