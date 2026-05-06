package com.axia.inventorymanagment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePointSettingsRequest {

    @NotNull(message = "defaultReturnRate is required")
    @DecimalMin(value = "0.01", message = "defaultReturnRate must be greater than 0")
    private BigDecimal defaultReturnRate;
}
