package com.axia.inventorymanagment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStageRequest {

    @Size(max = 50, message = "Stage name must not exceed 50 characters")
    private String stageName;

    @Min(value = 0, message = "minPoints must be 0 or greater")
    private Integer minPoints;

    @DecimalMin(value = "0.01", message = "returnRate must be greater than 0")
    private BigDecimal returnRate;

    @Min(value = 1, message = "displayOrder must be 1 or greater")
    private Integer displayOrder;
}
