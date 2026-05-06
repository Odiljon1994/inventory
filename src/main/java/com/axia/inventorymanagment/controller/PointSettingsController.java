package com.axia.inventorymanagment.controller;

import com.axia.inventorymanagment.dto.PointSettingsResponse;
import com.axia.inventorymanagment.dto.UpdatePointSettingsRequest;
import com.axia.inventorymanagment.service.PointSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/point-settings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Point Settings", description = "APIs for managing point settings (Admin only)")
@SecurityRequirement(name = "Bearer Authentication")
public class PointSettingsController {

    private final PointSettingsService pointSettingsService;

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Get point settings", description = "Returns the current default point return rate.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Point settings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role")
    })
    public ResponseEntity<PointSettingsResponse> getPointSettings() {
        return ResponseEntity.ok(pointSettingsService.getPointSettings());
    }

    @PutMapping
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Update point settings", description = "Updates the default point return rate.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Point settings updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role")
    })
    public ResponseEntity<PointSettingsResponse> updatePointSettings(@Valid @RequestBody UpdatePointSettingsRequest request) {
        log.info("Updating default point return rate to {}", request.getDefaultReturnRate());
        return ResponseEntity.ok(pointSettingsService.updatePointSettings(request));
    }
}
