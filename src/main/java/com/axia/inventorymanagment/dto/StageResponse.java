package com.axia.inventorymanagment.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageResponse {
    private Integer stageId;
    private String stageName;
    private Integer minPoints;
    private BigDecimal returnRate;
    private Integer displayOrder;
}
