package com.woodify.service;

import com.woodify.model.Produk;
import java.util.List;

public interface ProdukService {
    void addProduct(Produk product);
    void updateProduct(Produk product);
    void deleteProduct(String id);
    Produk getProductById(String id);
    List<Produk> getAllProducts();
    List<Produk> searchProducts(String keyword);
    List<Produk> getCriticalStockProducts(int limit);
}
