package com.axia.inventorymanagment.service;

import com.axia.inventorymanagment.dto.CreateDeliveryNoteRequest;
import com.axia.inventorymanagment.dto.DeliveryNoteDetailResponse;
import com.axia.inventorymanagment.dto.DeliveryNoteListResponse;
import com.axia.inventorymanagment.dto.ReceiveDeliveryNoteResponse;
import com.axia.inventorymanagment.entity.*;
import com.axia.inventorymanagment.exception.DeliveryNoteNotFoundException;
import com.axia.inventorymanagment.exception.ProductNotFoundException;
import com.axia.inventorymanagment.exception.StoreNotFoundException;
import com.axia.inventorymanagment.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryNoteService {

    private final DeliveryNoteRepository deliveryNoteRepository;
    private final DeliveryNoteItemRepository deliveryNoteItemRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final LotRepository lotRepository;
    private final InventoryStockRepository inventoryStockRepository;
    private final InventoryStockLogRepository inventoryStockLogRepository;

    public List<DeliveryNoteListResponse> getDeliveryNotes(String status) {
        return deliveryNoteRepository.findByStatusFilter(status)
                .stream()
                .map(this::mapToListResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeliveryNoteDetailResponse createDeliveryNote(CreateDeliveryNoteRequest request, User adminUser) {
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new StoreNotFoundException("Store with ID " + request.getStoreId() + " not found"));
        if (!Boolean.TRUE.equals(store.getIsActive())) {
            throw new IllegalArgumentException("Store with ID " + request.getStoreId() + " is not active");
        }

        String noteCode = generateNoteCode();

        DeliveryNote note = DeliveryNote.builder()
                .noteCode(noteCode)
                .toStore(store)
                .fromUser(adminUser)
                .status("pending")
                .build();
        note = deliveryNoteRepository.save(note);

        for (CreateDeliveryNoteRequest.LineItem lineItem : request.getLines()) {
            Product product = productRepository.findById(lineItem.getSkuId())
                    .orElseThrow(() -> new ProductNotFoundException("Product with SKU " + lineItem.getSkuId() + " not found"));
            if (!Boolean.TRUE.equals(product.getIsActive())) {
                throw new IllegalArgumentException("Product with SKU " + lineItem.getSkuId() + " is not active");
            }

            DeliveryNoteItem item = DeliveryNoteItem.builder()
                    .deliveryNote(note)
                    .product(product)
                    .quantity(lineItem.getQuantity())
                    .unitPrice(lineItem.getUnitPrice())
                    .build();
            deliveryNoteItemRepository.save(item);
        }

        return getDeliveryNoteDetail(noteCode);
    }

    public DeliveryNoteDetailResponse getDeliveryNoteDetail(String noteCode) {
        DeliveryNote note = deliveryNoteRepository.findByNoteCode(noteCode)
                .orElseThrow(() -> new DeliveryNoteNotFoundException("Delivery note " + noteCode + " not found"));

        List<DeliveryNoteItem> items = deliveryNoteItemRepository.findByDeliveryNote(note);

        List<DeliveryNoteDetailResponse.LineItem> lineItems = items.stream()
                .map(item -> DeliveryNoteDetailResponse.LineItem.builder()
                        .skuId(item.getProduct().getSkuId())
                        .productName(item.getProduct().getProductName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
                .collect(Collectors.toList());

        return DeliveryNoteDetailResponse.builder()
                .noteId(note.getNoteCode())
                .storeId(note.getToStore().getStoreId())
                .storeName(note.getToStore().getStoreName())
                .status(note.getStatus())
                .createdAt(note.getCreatedAt())
                .receivedAt(note.getAcceptedAt())
                .lines(lineItems)
                .build();
    }

    @Transactional
    public ReceiveDeliveryNoteResponse receiveDeliveryNote(String noteCode) {
        DeliveryNote note = deliveryNoteRepository.findByNoteCode(noteCode)
                .orElseThrow(() -> new DeliveryNoteNotFoundException("Delivery note " + noteCode + " not found"));

        if ("received".equals(note.getStatus())) {
            throw new IllegalArgumentException("Delivery note " + noteCode + " is already received");
        }

        List<DeliveryNoteItem> items = deliveryNoteItemRepository.findByDeliveryNote(note);
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        for (DeliveryNoteItem item : items) {
            String lotCode = generateLotCode(dateStr);

            Lot lot = Lot.builder()
                    .product(item.getProduct())
                    .lotCode(lotCode)
                    .purchasePrice(item.getUnitPrice())
                    .avgUnitPrice(item.getUnitPrice())
                    .initialQuantity(item.getQuantity())
                    .build();
            lot = lotRepository.save(lot);

            InventoryStock stock = InventoryStock.builder()
                    .lot(lot)
                    .store(note.getToStore())
                    .quantity(item.getQuantity())
                    .build();
            stock = inventoryStockRepository.save(stock);

            InventoryStockLog stockLog = InventoryStockLog.builder()
                    .stock(stock)
                    .changeType("INBOUND")
                    .quantityBefore(0)
                    .quantityAfter(item.getQuantity())
                    .delta(item.getQuantity())
                    .deliveryNoteId(note.getDeliveryNoteId())
                    .build();
            inventoryStockLogRepository.save(stockLog);

            item.setLot(lot);
            deliveryNoteItemRepository.save(item);
        }

        LocalDateTime receivedAt = LocalDateTime.now();
        note.setStatus("received");
        note.setAcceptedAt(receivedAt);
        deliveryNoteRepository.save(note);

        return ReceiveDeliveryNoteResponse.builder()
                .noteId(note.getNoteCode())
                .status("received")
                .receivedAt(receivedAt)
                .build();
    }

    private String generateNoteCode() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "DN-" + dateStr + "-";
        List<String> latest = deliveryNoteRepository.findLatestNoteCodeByPrefix(prefix + "%", PageRequest.of(0, 1));
        int sequence = 1;
        if (!latest.isEmpty()) {
            sequence = Integer.parseInt(latest.get(0).substring(prefix.length())) + 1;
        }
        return prefix + String.format("%03d", sequence);
    }

    private String generateLotCode(String dateStr) {
        String prefix = "LOT-" + dateStr + "-";
        List<String> latest = lotRepository.findLatestLotCodeByPrefix(prefix + "%", PageRequest.of(0, 1));
        int sequence = 1;
        if (!latest.isEmpty()) {
            sequence = Integer.parseInt(latest.get(0).substring(prefix.length())) + 1;
        }
        return prefix + String.format("%03d", sequence);
    }

    private DeliveryNoteListResponse mapToListResponse(DeliveryNote note) {
        List<DeliveryNoteItem> items = deliveryNoteItemRepository.findByDeliveryNote(note);
        return DeliveryNoteListResponse.builder()
                .noteId(note.getNoteCode())
                .storeId(note.getToStore().getStoreId())
                .storeName(note.getToStore().getStoreName())
                .status(note.getStatus())
                .createdAt(note.getCreatedAt())
                .receivedAt(note.getAcceptedAt())
                .lineCount(items.size())
                .build();
    }
}
