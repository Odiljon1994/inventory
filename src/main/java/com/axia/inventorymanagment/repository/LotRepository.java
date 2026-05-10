package com.axia.inventorymanagment.repository;

import com.axia.inventorymanagment.entity.Lot;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {

    @Query("SELECT l.lotCode FROM Lot l WHERE l.lotCode LIKE :prefix ORDER BY l.lotCode DESC")
    List<String> findLatestLotCodeByPrefix(@Param("prefix") String prefix, Pageable pageable);
}
