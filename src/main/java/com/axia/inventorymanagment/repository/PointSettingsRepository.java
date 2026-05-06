package com.axia.inventorymanagment.repository;

import com.axia.inventorymanagment.entity.PointSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointSettingsRepository extends JpaRepository<PointSettings, String> {
}
