package com.axia.inventorymanagment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointGrantRequest {

    @NotBlank(message = "memberId is required")
    private String memberId;

    @NotNull(message = "points is required")
    private Integer points;

    @Size(max = 255, message = "reason must not exceed 255 characters")
    private String reason;
}
