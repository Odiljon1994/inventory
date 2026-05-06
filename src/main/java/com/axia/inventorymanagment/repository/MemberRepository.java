package com.axia.inventorymanagment.repository;

import com.axia.inventorymanagment.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

    Optional<Member> findByMemberCode(String memberCode);

    @Query("SELECT m FROM Member m WHERE " +
           "(:stageId IS NULL OR m.stage.stageId = :stageId) AND " +
           "(:search IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(m.memberCode) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Member> findWithFilters(@Param("stageId") Integer stageId,
                                 @Param("search") String search,
                                 Pageable pageable);

    @Query("SELECT m FROM Member m WHERE " +
           "(:stageId IS NULL OR m.stage.stageId = :stageId) AND " +
           "(:search IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(m.memberCode) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Member> findAllWithFilters(@Param("stageId") Integer stageId,
                                    @Param("search") String search);
}
