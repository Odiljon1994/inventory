package com.axia.inventorymanagment.controller;

import com.axia.inventorymanagment.dto.DashboardResponse;
import com.axia.inventorymanagment.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dashboard", description = "Dashboard statistics API (Admin only)")
@SecurityRequirement(name = "Bearer Authentication")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    @Operation(
            summary = "Get dashboard statistics",
            description = "Retrieves monthly sales, sold count, low stock SKU count, and unknown inventory changes. Admin only."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard data retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters (missing or out-of-range year/month)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DashboardResponse> getDashboard(
            @RequestParam int year,
            @RequestParam int month) {
        log.info("Dashboard request: year={}, month={}", year, month);
        return ResponseEntity.ok(dashboardService.getDashboard(year, month));
    }
}
