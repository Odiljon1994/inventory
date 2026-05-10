package com.axia.inventorymanagment.controller;

import com.axia.inventorymanagment.dto.CreateDeliveryNoteRequest;
import com.axia.inventorymanagment.dto.DeliveryNoteDetailResponse;
import com.axia.inventorymanagment.dto.DeliveryNoteListResponse;
import com.axia.inventorymanagment.dto.ReceiveDeliveryNoteResponse;
import com.axia.inventorymanagment.entity.User;
import com.axia.inventorymanagment.service.DeliveryNoteService;
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

import java.util.List;

@RestController
@RequestMapping("/api/admin/delivery-notes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Delivery Note Management", description = "APIs for managing inbound delivery notes (Admin only)")
@SecurityRequirement(name = "Bearer Authentication")
public class DeliveryNoteController {

    private final DeliveryNoteService deliveryNoteService;

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Get delivery note list", description = "Returns all delivery notes. Optionally filter by status (pending/received).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery notes retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role")
    })
    public ResponseEntity<List<DeliveryNoteListResponse>> getDeliveryNotes(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(deliveryNoteService.getDeliveryNotes(status));
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Create delivery note", description = "Creates a new delivery note with line items.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Delivery note created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or empty lines"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role"),
            @ApiResponse(responseCode = "404", description = "Store or SKU not found")
    })
    public ResponseEntity<DeliveryNoteDetailResponse> createDeliveryNote(
            @Valid @RequestBody CreateDeliveryNoteRequest request,
            @AuthenticationPrincipal User currentUser) {
        log.info("Creating delivery note for store {}", request.getStoreId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deliveryNoteService.createDeliveryNote(request, currentUser));
    }

    @GetMapping("/{noteId}")
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Get delivery note detail", description = "Returns full detail of a delivery note including all line items.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery note retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role"),
            @ApiResponse(responseCode = "404", description = "Delivery note not found")
    })
    public ResponseEntity<DeliveryNoteDetailResponse> getDeliveryNoteDetail(@PathVariable String noteId) {
        return ResponseEntity.ok(deliveryNoteService.getDeliveryNoteDetail(noteId));
    }

    @PatchMapping("/{noteId}/receive")
    @PreAuthorize("hasRole('admin')")
    @Operation(summary = "Confirm receipt", description = "Marks delivery note as received, creates lots, and updates inventory stock.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Receipt confirmed successfully"),
            @ApiResponse(responseCode = "400", description = "Delivery note already received"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role"),
            @ApiResponse(responseCode = "404", description = "Delivery note not found")
    })
    public ResponseEntity<ReceiveDeliveryNoteResponse> receiveDeliveryNote(@PathVariable String noteId) {
        log.info("Confirming receipt for delivery note {}", noteId);
        return ResponseEntity.ok(deliveryNoteService.receiveDeliveryNote(noteId));
    }
}
