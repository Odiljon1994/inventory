package com.axia.inventorymanagment.service;

import com.axia.inventorymanagment.dto.PointSettingsResponse;
import com.axia.inventorymanagment.dto.UpdatePointSettingsRequest;
import com.axia.inventorymanagment.entity.PointSettings;
import com.axia.inventorymanagment.repository.PointSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointSettingsService {

    private static final String DEFAULT_RETURN_RATE_KEY = "default_return_rate";

    private final PointSettingsRepository pointSettingsRepository;

    public PointSettingsResponse getPointSettings() {
        PointSettings settings = pointSettingsRepository.findById(DEFAULT_RETURN_RATE_KEY)
                .orElseThrow(() -> new IllegalStateException("Point settings not configured"));
        return PointSettingsResponse.builder()
                .defaultReturnRate(new BigDecimal(settings.getSettingValue()))
                .updatedAt(settings.getUpdatedAt())
                .build();
    }

    @Transactional
    public PointSettingsResponse updatePointSettings(UpdatePointSettingsRequest request) {
        PointSettings settings = pointSettingsRepository.findById(DEFAULT_RETURN_RATE_KEY)
                .orElseThrow(() -> new IllegalStateException("Point settings not configured"));
        settings.setSettingValue(request.getDefaultReturnRate().toPlainString());
        settings.setUpdatedAt(LocalDateTime.now());
        PointSettings saved = pointSettingsRepository.save(settings);
        return PointSettingsResponse.builder()
                .defaultReturnRate(new BigDecimal(saved.getSettingValue()))
                .updatedAt(saved.getUpdatedAt())
                .build();
    }
}
