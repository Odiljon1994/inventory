package com.axia.inventorymanagment.service;

import com.axia.inventorymanagment.dto.CreateMemberRequest;
import com.axia.inventorymanagment.dto.MemberDetailResponse;
import com.axia.inventorymanagment.dto.MemberListResponse;
import com.axia.inventorymanagment.entity.Member;
import com.axia.inventorymanagment.entity.MemberStage;
import com.axia.inventorymanagment.exception.MemberNotFoundException;
import com.axia.inventorymanagment.repository.MemberRepository;
import com.axia.inventorymanagment.repository.MemberStageRepository;
import com.axia.inventorymanagment.repository.PointTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberStageRepository memberStageRepository;
    private final PointTransactionRepository pointTransactionRepository;

    public MemberDetailResponse createMember(CreateMemberRequest request) {
        String memberCode = generateMemberCode();
        MemberStage defaultStage = memberStageRepository.findAllByOrderByDisplayOrderAsc()
                .stream().findFirst().orElse(null);

        Member member = Member.builder()
                .memberCode(memberCode)
                .name(request.getName())
                .tel(request.getTel())
                .currentPoints(0)
                .stage(defaultStage)
                .build();

        member = memberRepository.save(member);
        log.info("Member created: {}", memberCode);

        return MemberDetailResponse.builder()
                .memberId(member.getMemberCode())
                .name(member.getName())
                .email(null)
                .points(0)
                .totalPointsEarned(0)
                .totalPointsUsed(0)
                .stage(defaultStage != null ? MemberDetailResponse.StageInfo.builder()
                        .stageId(defaultStage.getStageId())
                        .stageName(defaultStage.getStageName())
                        .returnRate(defaultStage.getReturnRate())
                        .build() : null)
                .createdAt(member.getCreatedAt())
                .lastActivityAt(null)
                .build();
    }

    private synchronized String generateMemberCode() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "MEM-" + dateStr + "-";
        List<String> latest = memberRepository.findLatestMemberCodeByPrefix(
                prefix + "%", PageRequest.of(0, 1));
        int sequence = 1;
        if (!latest.isEmpty()) {
            sequence = Integer.parseInt(latest.get(0).substring(prefix.length())) + 1;
        }
        return prefix + String.format("%03d", sequence);
    }

    public MemberListResponse getMembers(int page, int limit, String search, Integer stageId) {
        PageRequest pageable = PageRequest.of(page - 1, limit);
        String searchPattern = (search != null && !search.isBlank()) ? "%" + search + "%" : null;
        Page<Member> memberPage = memberRepository.findWithFilters(stageId, searchPattern, pageable);

        Integer totalMembers = (int) memberRepository.count();
        Integer totalPointsGranted = pointTransactionRepository.sumAllPositivePoints();

        List<MemberListResponse.MemberSummary> summaries = memberPage.getContent()
                .stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());

        return MemberListResponse.builder()
                .totalMembers(totalMembers)
                .totalPointsGranted(totalPointsGranted)
                .members(summaries)
                .total((int) memberPage.getTotalElements())
                .page(page)
                .limit(limit)
                .build();
    }

    public MemberDetailResponse getMemberDetail(String memberCode) {
        Member member = memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberCode + " not found"));

        Integer totalPointsEarned = pointTransactionRepository.sumEarnedPoints(member);
        Integer totalPointsUsed = pointTransactionRepository.sumUsedPoints(member);
        LocalDateTime lastActivityAt = pointTransactionRepository.findLastActivityAt(member).orElse(null);

        MemberDetailResponse.StageInfo stageInfo = null;
        if (member.getStage() != null) {
            stageInfo = MemberDetailResponse.StageInfo.builder()
                    .stageId(member.getStage().getStageId())
                    .stageName(member.getStage().getStageName())
                    .returnRate(member.getStage().getReturnRate())
                    .build();
        }

        String email = (member.getUser() != null) ? member.getUser().getEmail() : null;

        return MemberDetailResponse.builder()
                .memberId(member.getMemberCode())
                .name(member.getName())
                .email(email)
                .points(member.getCurrentPoints())
                .totalPointsEarned(totalPointsEarned)
                .totalPointsUsed(totalPointsUsed)
                .stage(stageInfo)
                .createdAt(member.getCreatedAt())
                .lastActivityAt(lastActivityAt)
                .build();
    }

    public String exportCsv(String search, Integer stageId) {
        String searchPattern = (search != null && !search.isBlank()) ? "%" + search + "%" : null;
        List<Member> members = memberRepository.findAllWithFilters(stageId, searchPattern);

        StringBuilder csv = new StringBuilder("﻿");
        csv.append("Member ID,Name,Email,Points,Stage,Registration Date\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Member m : members) {
            String email = (m.getUser() != null) ? m.getUser().getEmail() : "";
            String stageName = (m.getStage() != null) ? m.getStage().getStageName() : "";
            String registrationDate = (m.getCreatedAt() != null) ? m.getCreatedAt().format(formatter) : "";

            csv.append(escapeCsv(m.getMemberCode())).append(",")
               .append(escapeCsv(m.getName())).append(",")
               .append(escapeCsv(email)).append(",")
               .append(m.getCurrentPoints()).append(",")
               .append(escapeCsv(stageName)).append(",")
               .append(registrationDate).append("\n");
        }

        return csv.toString();
    }

    private MemberListResponse.MemberSummary mapToSummary(Member member) {
        MemberListResponse.StageInfo stageInfo = null;
        if (member.getStage() != null) {
            stageInfo = MemberListResponse.StageInfo.builder()
                    .stageId(member.getStage().getStageId())
                    .stageName(member.getStage().getStageName())
                    .build();
        }
        String email = (member.getUser() != null) ? member.getUser().getEmail() : null;

        return MemberListResponse.MemberSummary.builder()
                .memberId(member.getMemberCode())
                .name(member.getName())
                .points(member.getCurrentPoints())
                .stage(stageInfo)
                .email(email)
                .createdAt(member.getCreatedAt())
                .build();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
