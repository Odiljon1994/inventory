package com.axia.inventorymanagment.service;

import com.axia.inventorymanagment.dto.CreateStoreRequest;
import com.axia.inventorymanagment.dto.StoreResponse;
import com.axia.inventorymanagment.entity.Store;
import com.axia.inventorymanagment.exception.DuplicateStoreNameException;
import com.axia.inventorymanagment.exception.StoreNotFoundException;
import com.axia.inventorymanagment.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreResponse createStore(CreateStoreRequest request) {
        log.info("Creating store with name: {}", request.getStoreName());

        if (storeRepository.existsByStoreName(request.getStoreName())) {
            log.warn("Store with name '{}' already exists", request.getStoreName());
            throw new DuplicateStoreNameException("Store with name '" + request.getStoreName() + "' already exists");
        }

        Store store = Store.builder()
                .storeName(request.getStoreName())
                .location(request.getLocation())
                .isActive(true)
                .build();

        Store savedStore = storeRepository.save(store);
        log.info("Store created successfully with ID: {}", savedStore.getStoreId());

        return mapToResponse(savedStore);
    }

    public List<StoreResponse> getAllStores() {
        log.info("Fetching all stores");
        return storeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public StoreResponse getStoreById(Integer storeId) {
        log.info("Fetching store with ID: {}", storeId);
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> {
                    log.warn("Store with ID {} not found", storeId);
                    return new StoreNotFoundException("Store with ID " + storeId + " not found");
                });
        return mapToResponse(store);
    }

    private StoreResponse mapToResponse(Store store) {
        return StoreResponse.builder()
                .storeId(store.getStoreId())
                .storeName(store.getStoreName())
                .location(store.getLocation())
                .isActive(store.getIsActive())
                .createdAt(store.getCreatedAt())
                .build();
    }
}
