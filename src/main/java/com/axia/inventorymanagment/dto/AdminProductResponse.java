package com.axia.inventorymanagment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Product response for ADMIN role - includes all fields")
public class AdminProductResponse {

    @Schema(description = "Unique SKU identifier")
    private String skuId;

    @Schema(description = "Name of the product")
    private String productName;

    @Schema(description = "Import route type")
    private String importRouteTag;

    @Schema(description = "Purchase price")
    private BigDecimal purchasePrice;

    @Schema(description = "Tariff rate percentage")
    private BigDecimal tariffRate;

    @Schema(description = "Average unit price")
    private BigDecimal avgUnitPrice;

    @Schema(description = "Lot price")
    private BigDecimal lotPrice;

    @Schema(description = "Whether the product is active")
    private Boolean isActive;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
}
