package com.woodify.service;

import com.woodify.model.Transaksi;
import java.util.Date;
import java.util.List;

public interface TransaksiService {
    void processTransaction(Transaksi transaksi);
    Transaksi getTransactionById(String id);
    List<Transaksi> getAllTransactions();
    List<Transaksi> getTransactionsByDateRange(Date start, Date end);
    String generateNewTransactionId();
}
