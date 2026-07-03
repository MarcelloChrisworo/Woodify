package com.woodify.repository;

import com.woodify.model.Produk;
import java.util.List;

public interface ProdukRepository extends CrudRepository<Produk, String> {
    List<Produk> searchByNamaOrId(String keyword);
    List<Produk> findCriticalStock(int limitStock);
}
