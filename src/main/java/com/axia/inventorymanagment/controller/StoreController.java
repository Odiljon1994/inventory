package com.axia.inventorymanagment.controller;

import com.axia.inventorymanagment.dto.CreateStoreRequest;
import com.axia.inventorymanagment.dto.StoreResponse;
import com.axia.inventorymanagment.service.StoreService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stores")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Store Management", description = "APIs for managing stores (Admin only)")
@SecurityRequirement(name = "Bearer Authentication")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new store", description = "Creates a new store. Only accessible by ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Store created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
            @ApiResponse(responseCode = "409", description = "Store with same name already exists")
    })
    public ResponseEntity<StoreResponse> createStore(@Valid @RequestBody CreateStoreRequest request) {
        log.info("Received request to create store: {}", request.getStoreName());
        StoreResponse response = storeService.createStore(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all stores", description = "Retrieves all stores. Only accessible by ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stores retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role")
    })
    public ResponseEntity<List<StoreResponse>> getAllStores() {
        log.info("Received request to get all stores");
        List<StoreResponse> stores = storeService.getAllStores();
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/{storeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get store by ID", description = "Retrieves a specific store by its ID. Only accessible by ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Store retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Store not found")
    })
    public ResponseEntity<StoreResponse> getStoreById(@PathVariable Integer storeId) {
        log.info("Received request to get store with ID: {}", storeId);
        StoreResponse store = storeService.getStoreById(storeId);
        return ResponseEntity.ok(store);
    }
}
