package com.woodify.repository;

import com.woodify.model.Transaksi;
import java.util.Date;
import java.util.List;

public interface TransaksiRepository extends CrudRepository<Transaksi, String> {
    List<Transaksi> findByDateRange(Date start, Date end);
}
