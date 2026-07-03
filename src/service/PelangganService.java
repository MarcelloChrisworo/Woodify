package com.woodify.service;

import com.woodify.model.Pelanggan;
import java.util.List;

public interface PelangganService {
    void addCustomer(Pelanggan customer);
    void updateCustomer(Pelanggan customer);
    void deleteCustomer(int id);
    Pelanggan getCustomerById(int id);
    List<Pelanggan> getAllCustomers();
    List<Pelanggan> searchCustomers(String keyword);
}
