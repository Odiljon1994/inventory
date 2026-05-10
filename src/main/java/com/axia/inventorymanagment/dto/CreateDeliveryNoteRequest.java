package com.axia.inventorymanagment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDeliveryNoteRequest {

    @NotNull(message = "storeId is required")
    private Integer storeId;

    @NotEmpty(message = "lines must have at least 1 item")
    @Valid
    private List<LineItem> lines;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LineItem {

        @NotBlank(message = "skuId is required")
        private String skuId;

        @NotNull(message = "quantity is required")
        @Min(value = 1, message = "quantity must be 1 or greater")
        private Integer quantity;

        @Builder.Default
        private BigDecimal unitPrice = BigDecimal.ZERO;
    }
}
