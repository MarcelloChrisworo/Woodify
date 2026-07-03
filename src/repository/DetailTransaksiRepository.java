package com.woodify.repository;

import com.woodify.model.DetailTransaksi;
import java.util.List;

public interface DetailTransaksiRepository {
    void save(DetailTransaksi detail);
    List<DetailTransaksi> findByTransaksiId(String transaksiId);
}
