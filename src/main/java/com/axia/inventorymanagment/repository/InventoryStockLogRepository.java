package com.axia.inventorymanagment.repository;

import com.axia.inventorymanagment.entity.InventoryStockLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryStockLogRepository extends JpaRepository<InventoryStockLog, Long> {
}
