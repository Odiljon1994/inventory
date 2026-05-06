package com.axia.inventorymanagment.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointSettingsResponse {
    private BigDecimal defaultReturnRate;
    private LocalDateTime updatedAt;
}
