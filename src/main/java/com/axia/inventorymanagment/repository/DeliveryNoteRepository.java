package com.axia.inventorymanagment.repository;

import com.axia.inventorymanagment.entity.DeliveryNote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryNoteRepository extends JpaRepository<DeliveryNote, Integer> {

    Optional<DeliveryNote> findByNoteCode(String noteCode);

    @Query("SELECT d FROM DeliveryNote d WHERE :status IS NULL OR d.status = :status ORDER BY d.createdAt DESC")
    List<DeliveryNote> findByStatusFilter(@Param("status") String status);

    @Query("SELECT d.noteCode FROM DeliveryNote d WHERE d.noteCode LIKE :prefix ORDER BY d.noteCode DESC")
    List<String> findLatestNoteCodeByPrefix(@Param("prefix") String prefix, Pageable pageable);
}
