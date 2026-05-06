package com.axia.inventorymanagment.service;

import com.axia.inventorymanagment.dto.StageResponse;
import com.axia.inventorymanagment.dto.UpdateStageRequest;
import com.axia.inventorymanagment.entity.MemberStage;
import com.axia.inventorymanagment.exception.StageNotFoundException;
import com.axia.inventorymanagment.repository.MemberStageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageService {

    private final MemberStageRepository memberStageRepository;

    public List<StageResponse> getAllStages() {
        return memberStageRepository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public StageResponse updateStage(Integer stageId, UpdateStageRequest request) {
        MemberStage stage = memberStageRepository.findById(stageId)
                .orElseThrow(() -> new StageNotFoundException("Stage with ID " + stageId + " not found"));

        if (request.getStageName() != null) stage.setStageName(request.getStageName());
        if (request.getMinPoints() != null) stage.setMinPoints(request.getMinPoints());
        if (request.getReturnRate() != null) stage.setReturnRate(request.getReturnRate());
        if (request.getDisplayOrder() != null) stage.setDisplayOrder(request.getDisplayOrder());

        return mapToResponse(memberStageRepository.save(stage));
    }

    private StageResponse mapToResponse(MemberStage stage) {
        return StageResponse.builder()
                .stageId(stage.getStageId())
                .stageName(stage.getStageName())
                .minPoints(stage.getMinPoints())
                .returnRate(stage.getReturnRate())
                .displayOrder(stage.getDisplayOrder())
                .build();
    }
}
