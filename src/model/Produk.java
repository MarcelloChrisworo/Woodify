package com.woodify.model;

import com.woodify.exception.ValidationException;

public class Produk {
    private String id;
    private String nama;
    private String kategori;
    private double harga;
    private int stok;
    private String deskripsi;

    public Produk() {
    }

    public Produk(String id, String nama, String kategori, double harga, int stok, String deskripsi) {
        setId(id);
        setNama(nama);
        setKategori(kategori);
        setHarga(harga);
        setStok(stok);
        setDeskripsi(deskripsi);
    }

    // Encapsulation dengan validasi
    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("ID Produk tidak boleh kosong.");
        }
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new ValidationException("Nama produk tidak boleh kosong.");
        }
        this.nama = nama;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        if (kategori == null || kategori.trim().isEmpty()) {
            throw new ValidationException("Kategori tidak boleh kosong.");
        }
        this.kategori = kategori;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        if (harga < 0) {
            throw new ValidationException("Harga tidak boleh negatif.");
        }
        this.harga = harga;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        if (stok < 0) {
            throw new ValidationException("Stok tidak boleh negatif.");
        }
        this.stok = stok;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    @Override
    public String toString() {
        return nama + " (Stok: " + stok + ")";
    }
}
