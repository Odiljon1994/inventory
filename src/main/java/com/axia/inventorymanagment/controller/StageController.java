package com.axia.inventorymanagment.controller;

import com.axia.inventorymanagment.dto.StageResponse;
import com.axia.inventorymanagment.dto.UpdateStageRequest;
import com.axia.inventorymanagment.service.StageService;
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

import java.util.List;

@RestController
@RequestMapping("/api/admin/stages")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Stage Management", description = "APIs for managing member stages (Admin only)")
@SecurityRequirement(name = "Bearer Authentication")
public class StageController {

    private final StageService stageService;

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Get all stages", description = "Returns all member stages sorted by display order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stages retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role")
    })
    public ResponseEntity<List<StageResponse>> getAllStages() {
        return ResponseEntity.ok(stageService.getAllStages());
    }

    @PutMapping("/{stageId}")
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Update stage settings", description = "Partially updates a stage. Only provided fields are updated.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stage updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role"),
            @ApiResponse(responseCode = "404", description = "Stage not found")
    })
    public ResponseEntity<StageResponse> updateStage(@PathVariable Integer stageId,
                                                     @Valid @RequestBody UpdateStageRequest request) {
        log.info("Updating stage {}", stageId);
        return ResponseEntity.ok(stageService.updateStage(stageId, request));
    }
}
