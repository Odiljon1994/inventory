package com.axia.inventorymanagment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for user login")
public class LoginRequest {

    @Schema(description = "Username (optional if email provided)", example = "john_doe")
    private String username;

    @Schema(description = "Email (optional if username provided)", example = "john@example.com")
    private String email;

    @Schema(description = "Password", example = "securePassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
