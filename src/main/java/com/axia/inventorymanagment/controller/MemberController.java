package com.axia.inventorymanagment.controller;

import com.axia.inventorymanagment.dto.CreateMemberRequest;
import com.axia.inventorymanagment.dto.MemberDetailResponse;
import com.axia.inventorymanagment.dto.MemberListResponse;
import com.axia.inventorymanagment.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Member Management", description = "APIs for managing members (Admin only)")
@SecurityRequirement(name = "Bearer Authentication")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Create member", description = "Creates a new member. Member code is auto-generated.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Member created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role")
    })
    public ResponseEntity<MemberDetailResponse> createMember(@Valid @RequestBody CreateMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMember(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Get member list", description = "Returns paginated member list with summary statistics.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Members retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role")
    })
    public ResponseEntity<MemberListResponse> getMembers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer stageId) {
        return ResponseEntity.ok(memberService.getMembers(page, limit, search, stageId));
    }

    // NOTE: /export must be declared before /{memberId} to prevent "export" being matched as a memberId
    @GetMapping("/export")
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Export member list as CSV", description = "Downloads member list as a UTF-8 CSV file.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CSV exported successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role")
    })
    public void exportCsv(@RequestParam(required = false) String search,
                          @RequestParam(required = false) Integer stageId,
                          HttpServletResponse response) throws IOException {
        String filename = "members_export_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        String csv = memberService.exportCsv(search, stageId);
        response.getWriter().write(csv);
    }

    @GetMapping("/{memberId}")
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Get member detail", description = "Returns detailed information for a specific member.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    public ResponseEntity<MemberDetailResponse> getMemberDetail(@PathVariable String memberId) {
        return ResponseEntity.ok(memberService.getMemberDetail(memberId));
    }
}
