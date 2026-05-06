package com.axia.inventorymanagment.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointHistoryResponse {
    private Integer total;
    private Integer page;
    private Integer limit;
    private List<PointHistoryItem> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PointHistoryItem {
        private Long transactionId;
        private String memberId;
        private String memberName;
        private Integer points;
        private String transactionType;
        private String reason;
        private LocalDateTime createdAt;
    }
}
