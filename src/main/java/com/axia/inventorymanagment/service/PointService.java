package com.axia.inventorymanagment.service;

import com.axia.inventorymanagment.dto.PointGrantRequest;
import com.axia.inventorymanagment.dto.PointGrantResponse;
import com.axia.inventorymanagment.dto.PointHistoryResponse;
import com.axia.inventorymanagment.entity.Member;
import com.axia.inventorymanagment.entity.MemberStage;
import com.axia.inventorymanagment.entity.PointTransaction;
import com.axia.inventorymanagment.entity.User;
import com.axia.inventorymanagment.exception.MemberNotFoundException;
import com.axia.inventorymanagment.repository.MemberRepository;
import com.axia.inventorymanagment.repository.MemberStageRepository;
import com.axia.inventorymanagment.repository.PointTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {

    private final PointTransactionRepository pointTransactionRepository;
    private final MemberRepository memberRepository;
    private final MemberStageRepository memberStageRepository;

    public PointHistoryResponse getPointHistory(String memberCode, int page, int limit,
                                                String transactionType, String dateFrom, String dateTo) {
        if (memberCode != null && memberRepository.findByMemberCode(memberCode).isEmpty()) {
            throw new MemberNotFoundException("Member with ID " + memberCode + " not found");
        }

        LocalDateTime from = (dateFrom != null) ? LocalDateTime.parse(dateFrom) : null;
        LocalDateTime to = (dateTo != null) ? LocalDateTime.parse(dateTo) : null;

        PageRequest pageable = PageRequest.of(page - 1, limit);
        Page<PointTransaction> txPage = pointTransactionRepository.findWithFilters(
                memberCode, transactionType, from, to, pageable);

        List<PointHistoryResponse.PointHistoryItem> items = txPage.getContent()
                .stream()
                .map(this::mapToHistoryItem)
                .collect(Collectors.toList());

        return PointHistoryResponse.builder()
                .total((int) txPage.getTotalElements())
                .page(page)
                .limit(limit)
                .items(items)
                .build();
    }

    @Transactional
    public PointGrantResponse grantPoints(PointGrantRequest request, User adminUser) {
        if (request.getPoints() == null || request.getPoints() == 0) {
            throw new IllegalArgumentException("points must not be 0");
        }

        Member member = memberRepository.findByMemberCode(request.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + request.getMemberId() + " not found"));

        PointTransaction tx = PointTransaction.builder()
                .member(member)
                .points(request.getPoints())
                .reason(request.getReason())
                .transactionType("MANUAL")
                .createdBy(adminUser)
                .build();
        PointTransaction saved = pointTransactionRepository.save(tx);

        int newBalance = member.getCurrentPoints() + request.getPoints();
        member.setCurrentPoints(newBalance);
        member.setStage(resolveStage(newBalance));
        memberRepository.save(member);

        return PointGrantResponse.builder()
                .transactionId(saved.getTransactionId())
                .memberId(member.getMemberCode())
                .memberName(member.getName())
                .pointsGranted(request.getPoints())
                .newBalance(newBalance)
                .reason(request.getReason())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    private MemberStage resolveStage(int currentPoints) {
        List<MemberStage> stages = memberStageRepository.findAllByOrderByDisplayOrderAsc();
        return stages.stream()
                .filter(s -> s.getMinPoints() <= currentPoints)
                .max(Comparator.comparing(MemberStage::getMinPoints))
                .orElse(stages.isEmpty() ? null : stages.get(0));
    }

    private PointHistoryResponse.PointHistoryItem mapToHistoryItem(PointTransaction tx) {
        return PointHistoryResponse.PointHistoryItem.builder()
                .transactionId(tx.getTransactionId())
                .memberId(tx.getMember().getMemberCode())
                .memberName(tx.getMember().getName())
                .points(tx.getPoints())
                .transactionType(tx.getTransactionType())
                .reason(tx.getReason())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}
