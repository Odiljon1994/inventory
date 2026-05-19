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

    @Query(value = "SELECT * FROM members WHERE " +
           "(:stageId IS NULL OR stage_id = :stageId) AND " +
           "(:search IS NULL OR name ILIKE CAST(:search AS text) OR member_code ILIKE CAST(:search AS text))",
           countQuery = "SELECT COUNT(*) FROM members WHERE " +
           "(:stageId IS NULL OR stage_id = :stageId) AND " +
           "(:search IS NULL OR name ILIKE CAST(:search AS text) OR member_code ILIKE CAST(:search AS text))",
           nativeQuery = true)
    Page<Member> findWithFilters(@Param("stageId") Integer stageId,
                                 @Param("search") String search,
                                 Pageable pageable);

    @Query(value = "SELECT * FROM members WHERE " +
           "(:stageId IS NULL OR stage_id = :stageId) AND " +
           "(:search IS NULL OR name ILIKE CAST(:search AS text) OR member_code ILIKE CAST(:search AS text))",
           nativeQuery = true)
    List<Member> findAllWithFilters(@Param("stageId") Integer stageId,
                                    @Param("search") String search);
}
