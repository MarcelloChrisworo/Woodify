package com.woodify.service.impl;

import com.woodify.exception.DataNotFoundException;
import com.woodify.exception.ValidationException;
import com.woodify.model.Produk;
import com.woodify.repository.ProdukRepository;
import com.woodify.repository.impl.ProdukRepositoryImpl;
import com.woodify.service.ProdukService;

import java.util.List;

public class ProdukServiceImpl implements ProdukService {
    private final ProdukRepository produkRepository;

    public ProdukServiceImpl() {
        this.produkRepository = new ProdukRepositoryImpl();
    }

    public ProdukServiceImpl(ProdukRepository produkRepository) {
        this.produkRepository = produkRepository;
    }

    @Override
    public void addProduct(Produk product) {
        if (produkRepository.findById(product.getId()) != null) {
            throw new ValidationException("Produk dengan ID '" + product.getId() + "' sudah ada.");
        }
        produkRepository.save(product);
    }

    @Override
    public void updateProduct(Produk product) {
        if (produkRepository.findById(product.getId()) == null) {
            throw new DataNotFoundException("Produk dengan ID '" + product.getId() + "' tidak ditemukan.");
        }
        produkRepository.update(product);
    }

    @Override
    public void deleteProduct(String id) {
        if (produkRepository.findById(id) == null) {
            throw new DataNotFoundException("Produk dengan ID '" + id + "' tidak ditemukan.");
        }
        produkRepository.delete(id);
    }

    @Override
    public Produk getProductById(String id) {
        Produk p = produkRepository.findById(id);
        if (p == null) {
            throw new DataNotFoundException("Produk dengan ID '" + id + "' tidak ditemukan.");
        }
        return p;
    }

    @Override
    public List<Produk> getAllProducts() {
        return produkRepository.findAll();
    }

    @Override
    public List<Produk> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return produkRepository.searchByNamaOrId(keyword);
    }

    @Override
    public List<Produk> getCriticalStockProducts(int limit) {
        return produkRepository.findCriticalStock(limit);
    }
}
