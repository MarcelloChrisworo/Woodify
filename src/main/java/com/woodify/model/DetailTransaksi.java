package com.woodify.model;

import com.woodify.exception.ValidationException;

public class DetailTransaksi {
    private int id;
    private String transaksiId;
    private String produkId;
    private int qty;
    private double hargaJual;
    private double subtotal;

    // Untuk referensi di memori Java
    private Produk produkObj;

    public DetailTransaksi() {
    }

    public DetailTransaksi(int id, String transaksiId, String produkId, int qty, double hargaJual, double subtotal) {
        this.id = id;
        this.transaksiId = transaksiId;
        this.produkId = produkId;
        setQty(qty);
        setHargaJual(hargaJual);
        this.subtotal = subtotal;
    }

    public DetailTransaksi(String produkId, int qty, double hargaJual) {
        this.produkId = produkId;
        setQty(qty);
        setHargaJual(hargaJual);
        calculateSubtotal();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTransaksiId() {
        return transaksiId;
    }

    public void setTransaksiId(String transaksiId) {
        this.transaksiId = transaksiId;
    }

    public String getProdukId() {
        return produkId;
    }

    public void setProdukId(String produkId) {
        this.produkId = produkId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        if (qty <= 0) {
            throw new ValidationException("Jumlah beli (qty) harus lebih dari 0.");
        }
        this.qty = qty;
        calculateSubtotal();
    }

    public double getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(double hargaJual) {
        if (hargaJual < 0) {
            throw new ValidationException("Harga jual tidak boleh negatif.");
        }
        this.hargaJual = hargaJual;
        calculateSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public Produk getProdukObj() {
        return produkObj;
    }

    public void setProdukObj(Produk produkObj) {
        this.produkObj = produkObj;
    }

    private void calculateSubtotal() {
        this.subtotal = this.qty * this.hargaJual;
    }
}
