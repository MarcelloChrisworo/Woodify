package com.woodify.service.impl;

import com.woodify.exception.DataNotFoundException;
import com.woodify.exception.ValidationException;
import com.woodify.model.Pelanggan;
import com.woodify.repository.PelangganRepository;
import com.woodify.repository.impl.PelangganRepositoryImpl;
import com.woodify.service.PelangganService;

import java.util.List;

public class PelangganServiceImpl implements PelangganService {
    private final PelangganRepository pelangganRepository;

    public PelangganServiceImpl() {
        this.pelangganRepository = new PelangganRepositoryImpl();
    }

    public PelangganServiceImpl(PelangganRepository pelangganRepository) {
        this.pelangganRepository = pelangganRepository;
    }

    @Override
    public void addCustomer(Pelanggan customer) {
        if (customer.getNama() == null || customer.getNama().trim().isEmpty()) {
            throw new ValidationException("Nama pelanggan wajib diisi.");
        }
        pelangganRepository.save(customer);
    }

    @Override
    public void updateCustomer(Pelanggan customer) {
        if (pelangganRepository.findById(customer.getId()) == null) {
            throw new DataNotFoundException("Pelanggan dengan ID " + customer.getId() + " tidak ditemukan.");
        }
        pelangganRepository.update(customer);
    }

    @Override
    public void deleteCustomer(int id) {
        if (id == 1) {
            throw new ValidationException("Pelanggan Umum (ID 1) tidak boleh dihapus.");
        }
        if (pelangganRepository.findById(id) == null) {
            throw new DataNotFoundException("Pelanggan dengan ID " + id + " tidak ditemukan.");
        }
        pelangganRepository.delete(id);
    }

    @Override
    public Pelanggan getCustomerById(int id) {
        Pelanggan c = pelangganRepository.findById(id);
        if (c == null) {
            throw new DataNotFoundException("Pelanggan dengan ID " + id + " tidak ditemukan.");
        }
        return c;
    }

    @Override
    public List<Pelanggan> getAllCustomers() {
        return pelangganRepository.findAll();
    }

    @Override
    public List<Pelanggan> searchCustomers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCustomers();
        }
        return pelangganRepository.searchByNamaOrTelepon(keyword);
    }
}
