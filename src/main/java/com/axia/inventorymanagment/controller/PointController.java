package com.axia.inventorymanagment.controller;

import com.axia.inventorymanagment.dto.PointGrantRequest;
import com.axia.inventorymanagment.dto.PointGrantResponse;
import com.axia.inventorymanagment.dto.PointHistoryResponse;
import com.axia.inventorymanagment.entity.User;
import com.axia.inventorymanagment.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/points")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Point Management", description = "APIs for managing member points (Admin only)")
@SecurityRequirement(name = "Bearer Authentication")
public class PointController {

    private final PointService pointService;

    @GetMapping("/history")
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Get point history", description = "Returns paginated point transaction history with optional filters.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "History retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role"),
            @ApiResponse(responseCode = "404", description = "Specified member not found")
    })
    public ResponseEntity<PointHistoryResponse> getPointHistory(
            @RequestParam(required = false) String memberId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        return ResponseEntity.ok(pointService.getPointHistory(memberId, page, limit, transactionType, dateFrom, dateTo));
    }

    @PostMapping("/grant")
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Grant points manually", description = "Manually grants or deducts points for a member.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Points granted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    public ResponseEntity<PointGrantResponse> grantPoints(@Valid @RequestBody PointGrantRequest request,
                                                          @AuthenticationPrincipal User currentUser) {
        log.info("Admin {} granting {} points to member {}", currentUser.getUsername(), request.getPoints(), request.getMemberId());
        return ResponseEntity.status(HttpStatus.CREATED).body(pointService.grantPoints(request, currentUser));
    }
}
