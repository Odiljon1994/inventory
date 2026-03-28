package com.axia.inventorymanagment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreResponse {

    private Integer storeId;
    private String storeName;
    private String location;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
