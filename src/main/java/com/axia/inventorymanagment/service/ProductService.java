package com.axia.inventorymanagment.service;

import com.axia.inventorymanagment.dto.AdminProductResponse;
import com.axia.inventorymanagment.dto.CreateProductRequest;
import com.axia.inventorymanagment.dto.StoreProductResponse;
import com.axia.inventorymanagment.entity.Product;
import com.axia.inventorymanagment.exception.DuplicateProductException;
import com.axia.inventorymanagment.exception.DuplicateSkuIdException;
import com.axia.inventorymanagment.exception.ProductNotFoundException;
import com.axia.inventorymanagment.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public AdminProductResponse createProduct(CreateProductRequest request) {
        log.info("Creating product with SKU: {}", request.getSkuId());

        if (productRepository.existsBySkuId(request.getSkuId())) {
            log.warn("SKU ID '{}' already exists", request.getSkuId());
            throw new DuplicateSkuIdException("SKU ID already exists");
        }

        if (productRepository.existsByProductNameAndImportRouteTag(
                request.getProductName(), request.getImportRouteTag())) {
            log.warn("Product with name '{}' and route '{}' already exists",
                    request.getProductName(), request.getImportRouteTag());
            throw new DuplicateProductException("Product with same name and route already exists");
        }

        Product product = Product.builder()
                .skuId(request.getSkuId())
                .productName(request.getProductName())
                .importRouteTag(request.getImportRouteTag())
                .purchasePrice(request.getPurchasePrice())
                .tariffRate(request.getTariffRate())
                .avgUnitPrice(request.getAvgUnitPrice())
                .lotPrice(request.getLotPrice())
                .isActive(true)
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with SKU: {}", savedProduct.getSkuId());

        return mapToAdminResponse(savedProduct);
    }

    public List<AdminProductResponse> getAllProductsForAdmin() {
        log.info("Fetching all products for admin");
        return productRepository.findAll()
                .stream()
                .map(this::mapToAdminResponse)
                .collect(Collectors.toList());
    }

    public List<StoreProductResponse> getAllProductsForStore() {
        log.info("Fetching active products for store");
        return productRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToStoreResponse)
                .collect(Collectors.toList());
    }

    public AdminProductResponse getProductByIdForAdmin(String skuId) {
        log.info("Fetching product with SKU: {} for admin", skuId);
        Product product = productRepository.findById(skuId)
                .orElseThrow(() -> {
                    log.warn("Product with SKU '{}' not found", skuId);
                    return new ProductNotFoundException("Product with SKU '" + skuId + "' not found");
                });
        return mapToAdminResponse(product);
    }

    public StoreProductResponse getProductByIdForStore(String skuId) {
        log.info("Fetching product with SKU: {} for store", skuId);
        Product product = productRepository.findBySkuIdAndIsActiveTrue(skuId)
                .orElseThrow(() -> {
                    log.warn("Active product with SKU '{}' not found", skuId);
                    return new ProductNotFoundException("Product with SKU '" + skuId + "' not found");
                });
        return mapToStoreResponse(product);
    }

    public void deactivateProduct(String skuId) {
        log.info("Deactivating product with SKU: {}", skuId);
        Product product = productRepository.findById(skuId)
                .orElseThrow(() -> {
                    log.warn("Product with SKU '{}' not found for deactivation", skuId);
                    return new ProductNotFoundException("Product with SKU '" + skuId + "' not found");
                });

        product.setIsActive(false);
        productRepository.save(product);
        log.info("Product with SKU '{}' deactivated successfully", skuId);
    }

    private AdminProductResponse mapToAdminResponse(Product product) {
        return AdminProductResponse.builder()
                .skuId(product.getSkuId())
                .productName(product.getProductName())
                .importRouteTag(product.getImportRouteTag())
                .purchasePrice(product.getPurchasePrice())
                .tariffRate(product.getTariffRate())
                .avgUnitPrice(product.getAvgUnitPrice())
                .lotPrice(product.getLotPrice())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .build();
    }

    private StoreProductResponse mapToStoreResponse(Product product) {
        return StoreProductResponse.builder()
                .skuId(product.getSkuId())
                .productName(product.getProductName())
                .importRouteTag(product.getImportRouteTag())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
