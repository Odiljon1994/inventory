package com.axia.inventorymanagment.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiveDeliveryNoteResponse {
    private String noteId;
    private String status;
    private LocalDateTime receivedAt;
}
