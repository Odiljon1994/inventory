package com.axia.inventorymanagment.repository;

import com.axia.inventorymanagment.entity.MemberStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberStageRepository extends JpaRepository<MemberStage, Integer> {
    List<MemberStage> findAllByOrderByDisplayOrderAsc();
}
