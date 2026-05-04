package com.axia.inventorymanagment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private Long monthlySales;
    private Long monthlySalesPrev;
    private Long totalSoldCount;
    private Long totalSoldCountPrev;
    private Long lowStockSkuCount;
    private Long unknownInventoryCount;
    private String unknownInventoryStore;
}
