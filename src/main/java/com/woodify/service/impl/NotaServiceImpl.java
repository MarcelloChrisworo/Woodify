package com.woodify.service.impl;

import com.woodify.model.DetailTransaksi;
import com.woodify.model.Transaksi;
import com.woodify.repository.PelangganRepository;
import com.woodify.repository.UserRepository;
import com.woodify.repository.impl.PelangganRepositoryImpl;
import com.woodify.repository.impl.UserRepositoryImpl;
import com.woodify.service.ReceiptPrinter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class NotaServiceImpl implements ReceiptPrinter {
    private final UserRepository userRepository;
    private final PelangganRepository pelangganRepository;
    private final NumberFormat rpFormat;

    public NotaServiceImpl() {
        this.userRepository = new UserRepositoryImpl();
        this.pelangganRepository = new PelangganRepositoryImpl();
        this.rpFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
    }

    @Override
    public String buildReceiptString(Transaksi transaksi) {
        // Lengkapi objek relasi jika null
        if (transaksi.getKasirObj() == null) {
            transaksi.setKasirObj(userRepository.findById(transaksi.getUserId()));
        }
        if (transaksi.getPelangganObj() == null && transaksi.getPelangganId() > 0) {
            transaksi.setPelangganObj(pelangganRepository.findById(transaksi.getPelangganId()));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        
        sb.append("==========================================\n");
        sb.append("               WOODIFY SHOP               \n");
        sb.append("         Furnitur & Home Decor UMKM        \n");
        sb.append("         Jl. Kayu Jati No. 99, Jepara      \n");
        sb.append("==========================================\n");
        sb.append("Nota No. : ").append(transaksi.getId()).append("\n");
        sb.append("Tanggal  : ").append(sdf.format(transaksi.getTanggal())).append("\n");
        sb.append("Kasir    : ").append(transaksi.getKasirObj() != null ? transaksi.getKasirObj().getNamaLengkap() : "Kasir").append("\n");
        sb.append("Pelanggan: ").append(transaksi.getPelangganObj() != null ? transaksi.getPelangganObj().getNama() : "Umum").append("\n");
        sb.append("------------------------------------------\n");
        sb.append(String.format("%-22s %-3s %-15s\n", "Item", "Qty", "Subtotal"));
        sb.append("------------------------------------------\n");

        for (DetailTransaksi item : transaksi.getDetails()) {
            String nama = item.getProdukObj() != null ? item.getProdukObj().getNama() : "Produk (" + item.getProdukId() + ")";
            if (nama.length() > 20) {
                nama = nama.substring(0, 18) + "..";
            }
            
            sb.append(String.format("%-22s %-3d %-15s\n", 
                    nama, 
                    item.getQty(), 
                    formatRupiah(item.getSubtotal())));
            sb.append(String.format("  @ %-37s\n", formatRupiah(item.getHargaJual())));
        }

        sb.append("------------------------------------------\n");
        sb.append(String.format("%-25s %-15s\n", "Total Belanja:", formatRupiah(transaksi.getTotalHarga())));
        sb.append(String.format("%-25s %-15s\n", "Jumlah Bayar :", formatRupiah(transaksi.getBayar())));
        sb.append(String.format("%-25s %-15s\n", "Kembalian    :", formatRupiah(transaksi.getKembalian())));
        sb.append("==========================================\n");
        sb.append("      Terima Kasih Atas Kunjungan Anda     \n");
        sb.append("       Furnitur Cantik untuk Rumah Anda    \n");
        sb.append("==========================================\n");
        
        return sb.toString();
    }

    private String formatRupiah(double val) {
        String res = rpFormat.format(val);
        // Rapikan format Rupiah bawaan Java (ganti Rp dengan space atau standar Indonesia)
        return res.replace("Rp", "Rp ").replace(",00", "");
    }
}
