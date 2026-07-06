package com.woodify.repository;

import com.woodify.model.Pelanggan;
import java.util.List;

public interface PelangganRepository extends CrudRepository<Pelanggan, Integer> {
    List<Pelanggan> searchByNamaOrTelepon(String keyword);
}
