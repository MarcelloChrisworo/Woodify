package com.woodify.service.impl;

import com.woodify.model.DetailTransaksi;
import com.woodify.model.Transaksi;
import com.woodify.repository.TransaksiRepository;
import com.woodify.repository.DetailTransaksiRepository;
import com.woodify.repository.impl.TransaksiRepositoryImpl;
import com.woodify.repository.impl.DetailTransaksiRepositoryImpl;
import com.woodify.service.LaporanService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LaporanServiceImpl implements LaporanService {
    private final TransaksiRepository transaksiRepository;
    private final DetailTransaksiRepository detailTransaksiRepository;

    public LaporanServiceImpl() {
        this.transaksiRepository = new TransaksiRepositoryImpl();
        this.detailTransaksiRepository = new DetailTransaksiRepositoryImpl();
    }

    @Override
    public Map<String, Object> generateSummaryReport(Date startDate, Date endDate) {
        List<Transaksi> transaksis = transaksiRepository.findByDateRange(startDate, endDate);
        
        double totalPendapatan = 0;
        int totalItemTerjual = 0;
        
        for (Transaksi trx : transaksis) {
            totalPendapatan += trx.getTotalHarga();
            
            // Hitung detail item terjual
            List<DetailTransaksi> details = detailTransaksiRepository.findByTransaksiId(trx.getId());
            for (DetailTransaksi detail : details) {
                totalItemTerjual += detail.getQty();
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalPendapatan", totalPendapatan);
        summary.put("jumlahTransaksi", transaksis.size());
        summary.put("totalItemTerjual", totalItemTerjual);
        
        return summary;
    }

    @Override
    public List<Transaksi> generateDetailedReport(Date startDate, Date endDate) {
        List<Transaksi> transaksis = transaksiRepository.findByDateRange(startDate, endDate);
        for (Transaksi trx : transaksis) {
            List<DetailTransaksi> details = detailTransaksiRepository.findByTransaksiId(trx.getId());
            trx.setDetails(details);
        }
        return transaksis;
    }
}
