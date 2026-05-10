package com.axia.inventorymanagment.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryNoteDetailResponse {
    private String noteId;
    private Integer storeId;
    private String storeName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime receivedAt;
    private List<LineItem> lines;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LineItem {
        private String skuId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}
