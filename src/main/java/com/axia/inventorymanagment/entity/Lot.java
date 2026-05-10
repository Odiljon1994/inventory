package com.axia.inventorymanagment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lot_id")
    private Long lotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_id", nullable = false)
    private Product product;

    @Column(name = "lot_code", unique = true, nullable = false, length = 100)
    private String lotCode;

    @Column(name = "purchase_price", nullable = false)
    private BigDecimal purchasePrice;

    @Builder.Default
    @Column(name = "tariff_rate")
    private BigDecimal tariffRate = BigDecimal.ZERO;

    @Column(name = "avg_unit_price")
    private BigDecimal avgUnitPrice;

    @Column(name = "initial_quantity", nullable = false)
    private Integer initialQuantity;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (tariffRate == null) tariffRate = BigDecimal.ZERO;
        if (avgUnitPrice == null) avgUnitPrice = purchasePrice;
    }
}
