package com.woodify.model;

import com.woodify.exception.ValidationException;

public class Pelanggan {
    private int id;
    private String nama;
    private String telepon;
    private String alamat;

    public Pelanggan() {
    }

    public Pelanggan(int id, String nama, String telepon, String alamat) {
        this.id = id;
        setNama(nama);
        setTelepon(telepon);
        setAlamat(alamat);
    }

    // Constructor untuk data pelanggan baru (belum punya ID dari database)
    public Pelanggan(String nama, String telepon, String alamat) {
        setNama(nama);
        setTelepon(telepon);
        setAlamat(alamat);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new ValidationException("Nama pelanggan tidak boleh kosong.");
        }
        this.nama = nama;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    @Override
    public String toString() {
        return nama;
    }
}
