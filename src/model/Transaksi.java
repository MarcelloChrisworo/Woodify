package com.woodify.model;

import com.woodify.exception.ValidationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transaksi {
    private String id;
    private int userId;
    private int pelangganId;
    private Date tanggal;
    private double totalHarga;
    private double bayar;
    private double kembalian;
    
    // Collection: Menyimpan detail item transaksi (relasi One-to-Many)
    private List<DetailTransaksi> details = new ArrayList<>();

    // Untuk referensi objek di Java
    private User kasirObj;
    private Pelanggan pelangganObj;

    public Transaksi() {
        this.tanggal = new Date();
    }

    public Transaksi(String id, int userId, int pelangganId, Date tanggal, double totalHarga, double bayar, double kembalian) {
        this.id = id;
        this.userId = userId;
        this.pelangganId = pelangganId;
        this.tanggal = tanggal;
        this.totalHarga = totalHarga;
        this.bayar = bayar;
        this.kembalian = kembalian;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPelangganId() {
        return pelangganId;
    }

    public void setPelangganId(int pelangganId) {
        this.pelangganId = pelangganId;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public double getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(double totalHarga) {
        if (totalHarga < 0) {
            throw new ValidationException("Total harga tidak boleh negatif.");
        }
        this.totalHarga = totalHarga;
    }

    public double getBayar() {
        return bayar;
    }

    public void setBayar(double bayar) {
        if (bayar < 0) {
            throw new ValidationException("Pembayaran tidak boleh kurang dari nol.");
        }
        this.bayar = bayar;
    }

    public double getKembalian() {
        return kembalian;
    }

    public void setKembalian(double kembalian) {
        this.kembalian = kembalian;
    }

    public List<DetailTransaksi> getDetails() {
        return details;
    }

    public void setDetails(List<DetailTransaksi> details) {
        this.details = details;
    }

    public void addDetail(DetailTransaksi detail) {
        this.details.add(detail);
        calculateTotal();
    }

    public void calculateTotal() {
        double calculatedTotal = 0;
        for (DetailTransaksi detail : details) {
            calculatedTotal += detail.getSubtotal();
        }
        this.totalHarga = calculatedTotal;
    }

    public void processPayment(double inputBayar) {
        if (inputBayar < this.totalHarga) {
            throw new ValidationException("Pembayaran kurang! Total belanja: " + totalHarga);
        }
        this.bayar = inputBayar;
        this.kembalian = inputBayar - this.totalHarga;
    }

    public User getKasirObj() {
        return kasirObj;
    }

    public void setKasirObj(User kasirObj) {
        this.kasirObj = kasirObj;
    }

    public Pelanggan getPelangganObj() {
        return pelangganObj;
    }

    public void setPelangganObj(Pelanggan pelangganObj) {
        this.pelangganObj = pelangganObj;
    }
}
