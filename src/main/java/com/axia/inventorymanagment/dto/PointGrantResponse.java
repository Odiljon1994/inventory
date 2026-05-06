package com.axia.inventorymanagment.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointGrantResponse {
    private Long transactionId;
    private String memberId;
    private String memberName;
    private Integer pointsGranted;
    private Integer newBalance;
    private String reason;
    private LocalDateTime createdAt;
}
