package com.axia.inventorymanagment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating a new product")
public class CreateProductRequest {

    @NotBlank(message = "SKU ID is required")
    @Schema(description = "Unique SKU identifier", example = "SKU-001")
    private String skuId;

    @NotBlank(message = "Product name is required")
    @Schema(description = "Name of the product", example = "Wireless Mouse")
    private String productName;

    @NotBlank(message = "Import route tag is required")
    @Pattern(regexp = "^(Regular|Hand-carry)$", message = "Import route tag must be 'Regular' or 'Hand-carry'")
    @Schema(description = "Import route type", allowableValues = {"Regular", "Hand-carry"}, example = "Regular")
    private String importRouteTag;
}
