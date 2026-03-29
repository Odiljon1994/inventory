package com.axia.inventorymanagment.repository;

import com.axia.inventorymanagment.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    boolean existsBySkuId(String skuId);

    boolean existsByProductNameAndImportRouteTag(String productName, String importRouteTag);

    List<Product> findByIsActiveTrue();

    Optional<Product> findBySkuIdAndIsActiveTrue(String skuId);
}
