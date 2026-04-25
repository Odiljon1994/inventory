package com.axia.inventorymanagment.controller;

import com.axia.inventorymanagment.dto.AdminProductResponse;
import com.axia.inventorymanagment.dto.CreateProductRequest;
import com.axia.inventorymanagment.dto.StoreProductResponse;
import com.axia.inventorymanagment.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Management", description = "APIs for managing products")
@SecurityRequirement(name = "Bearer Authentication")
public class ProductController {

    private final ProductService productService;

    // ==================== ADMIN ENDPOINTS ====================

    @PostMapping("/admin/products")
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Create a new product", description = "Creates a new product. Only accessible by admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role"),
            @ApiResponse(responseCode = "409", description = "Duplicate SKU ID or product name with same route")
    })
    public ResponseEntity<AdminProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        log.info("Received request to create product with SKU: {}", request.getSkuId());
        AdminProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/admin/products")
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Get all products for admin", description = "Retrieves all products including inactive ones. Only accessible by admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role")
    })
    public ResponseEntity<List<AdminProductResponse>> getAllProductsForAdmin() {
        log.info("Received request to get all products for admin");
        List<AdminProductResponse> products = productService.getAllProductsForAdmin();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/admin/products/{skuId}")
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Get product by SKU ID for admin", description = "Retrieves a specific product by SKU ID. Only accessible by admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<AdminProductResponse> getProductByIdForAdmin(@PathVariable String skuId) {
        log.info("Received request to get product with SKU: {} for admin", skuId);
        AdminProductResponse product = productService.getProductByIdForAdmin(skuId);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/admin/products/{skuId}")
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Deactivate a product", description = "Soft deletes a product by setting isActive to false. Only accessible by admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deactivated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Map<String, String>> deactivateProduct(@PathVariable String skuId) {
        log.info("Received request to deactivate product with SKU: {}", skuId);
        productService.deactivateProduct(skuId);
        return ResponseEntity.ok(Map.of("message", "Product deactivated successfully"));
    }

    // ==================== STORE ENDPOINTS ====================

    @GetMapping("/store/products")
    @PreAuthorize("hasRole('staff')")
    @Operation(summary = "Get all active products for store", description = "Retrieves all active products without cost information. Only accessible by staff role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires staff role")
    })
    public ResponseEntity<List<StoreProductResponse>> getAllProductsForStore() {
        log.info("Received request to get all products for store");
        List<StoreProductResponse> products = productService.getAllProductsForStore();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/store/products/{skuId}")
    @PreAuthorize("hasRole('staff')")
    @Operation(summary = "Get active product by SKU ID for store", description = "Retrieves a specific active product by SKU ID without cost information. Only accessible by staff role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires staff role"),
            @ApiResponse(responseCode = "404", description = "Product not found or inactive")
    })
    public ResponseEntity<StoreProductResponse> getProductByIdForStore(@PathVariable String skuId) {
        log.info("Received request to get product with SKU: {} for store", skuId);
        StoreProductResponse product = productService.getProductByIdForStore(skuId);
        return ResponseEntity.ok(product);
    }
}
