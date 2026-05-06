package com.axia.inventorymanagment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "member_stages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stage_id")
    private Integer stageId;

    @Column(name = "stage_name", nullable = false, length = 50)
    private String stageName;

    @Column(name = "min_points", nullable = false)
    private Integer minPoints;

    @Column(name = "return_rate", nullable = false)
    private BigDecimal returnRate;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;
}
