package com.woodify.service;

import com.woodify.model.Transaksi;

public interface ReceiptPrinter {
    String buildReceiptString(Transaksi transaksi);
}
