package com.axia.inventorymanagment.repository;

import com.axia.inventorymanagment.entity.DeliveryNote;
import com.axia.inventorymanagment.entity.DeliveryNoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryNoteItemRepository extends JpaRepository<DeliveryNoteItem, Integer> {
    List<DeliveryNoteItem> findByDeliveryNote(DeliveryNote deliveryNote);
}
