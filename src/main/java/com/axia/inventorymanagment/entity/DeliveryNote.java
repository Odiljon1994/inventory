package com.axia.inventorymanagment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "delivery_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_note_id")
    private Integer deliveryNoteId;

    @Column(name = "note_code", unique = true, length = 30)
    private String noteCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_store_id", nullable = false)
    private Store toStore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id")
    private User fromUser;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @OneToMany(mappedBy = "deliveryNote", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeliveryNoteItem> items;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = "pending";
    }
}
