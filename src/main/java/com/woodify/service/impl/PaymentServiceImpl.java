package com.woodify.service.impl;

import com.woodify.exception.ValidationException;
import com.woodify.model.Transaksi;
import com.woodify.service.PaymentService;

public class PaymentServiceImpl implements PaymentService {
    @Override
    public void processPayment(Transaksi transaksi, double amountPaid) {
        if (amountPaid < transaksi.getTotalHarga()) {
            throw new ValidationException("Pembayaran kurang! Total belanja: " + transaksi.getTotalHarga());
        }
        transaksi.setBayar(amountPaid);
        transaksi.setKembalian(amountPaid - transaksi.getTotalHarga());
    }
}
