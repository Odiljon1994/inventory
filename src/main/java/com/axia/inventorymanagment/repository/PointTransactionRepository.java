package com.axia.inventorymanagment.repository;

import com.axia.inventorymanagment.entity.Member;
import com.axia.inventorymanagment.entity.PointTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    @Query("SELECT pt FROM PointTransaction pt WHERE " +
           "(:memberCode IS NULL OR pt.member.memberCode = :memberCode) AND " +
           "(:transactionType IS NULL OR pt.transactionType = :transactionType) AND " +
           "(:dateFrom IS NULL OR pt.createdAt >= :dateFrom) AND " +
           "(:dateTo IS NULL OR pt.createdAt <= :dateTo)")
    Page<PointTransaction> findWithFilters(@Param("memberCode") String memberCode,
                                           @Param("transactionType") String transactionType,
                                           @Param("dateFrom") LocalDateTime dateFrom,
                                           @Param("dateTo") LocalDateTime dateTo,
                                           Pageable pageable);

    @Query("SELECT COALESCE(SUM(pt.points), 0) FROM PointTransaction pt WHERE pt.points > 0")
    Integer sumAllPositivePoints();

    @Query("SELECT COALESCE(SUM(pt.points), 0) FROM PointTransaction pt WHERE pt.member = :member AND pt.points > 0")
    Integer sumEarnedPoints(@Param("member") Member member);

    @Query("SELECT COALESCE(SUM(ABS(pt.points)), 0) FROM PointTransaction pt WHERE pt.member = :member AND pt.points < 0")
    Integer sumUsedPoints(@Param("member") Member member);

    @Query("SELECT MAX(pt.createdAt) FROM PointTransaction pt WHERE pt.member = :member")
    Optional<LocalDateTime> findLastActivityAt(@Param("member") Member member);
}
