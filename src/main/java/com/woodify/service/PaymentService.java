package com.woodify.service;

import com.woodify.model.Transaksi;

public interface PaymentService {
    void processPayment(Transaksi transaksi, double amountPaid);
}
