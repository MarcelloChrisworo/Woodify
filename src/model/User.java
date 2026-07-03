package com.woodify.model;

public abstract class User {
    private int id;
    private String username;
    private String password;
    private String namaLengkap;
    private String role;

    public User(int id, String username, String password, String namaLengkap, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.namaLengkap = namaLengkap;
        this.role = role;
    }

    // Encapsulation: Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Polimorfisme: Setiap role user akan mengimplementasikan metode ini secara berbeda
    public abstract String getRoleDisplay();
    public abstract boolean hasAccessToReports();
    public abstract boolean hasAccessToProductManagement();
}
