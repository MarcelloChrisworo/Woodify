package com.woodify.service;

import com.woodify.model.Transaksi;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ReportGenerator {
    // Polimorfisme interface: mendukung laporan ringkas (summary map) dan laporan detail (list transaksi)
    Map<String, Object> generateSummaryReport(Date startDate, Date endDate);
    List<Transaksi> generateDetailedReport(Date startDate, Date endDate);
}
