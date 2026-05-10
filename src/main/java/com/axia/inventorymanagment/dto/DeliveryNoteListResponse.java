package com.axia.inventorymanagment.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryNoteListResponse {
    private String noteId;
    private Integer storeId;
    private String storeName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime receivedAt;
    private Integer lineCount;
}
