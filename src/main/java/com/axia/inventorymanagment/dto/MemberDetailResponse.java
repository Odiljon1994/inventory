package com.axia.inventorymanagment.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDetailResponse {
    private String memberId;
    private String name;
    private String email;
    private Integer points;
    private Integer totalPointsEarned;
    private Integer totalPointsUsed;
    private StageInfo stage;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivityAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StageInfo {
        private Integer stageId;
        private String stageName;
        private BigDecimal returnRate;
    }
}
