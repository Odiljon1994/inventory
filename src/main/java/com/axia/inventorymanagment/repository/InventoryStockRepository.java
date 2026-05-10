package com.axia.inventorymanagment.repository;

import com.axia.inventorymanagment.entity.InventoryStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryStockRepository extends JpaRepository<InventoryStock, Long> {
}
