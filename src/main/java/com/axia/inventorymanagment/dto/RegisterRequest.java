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
@Schema(description = "Request object for user registration")
public class RegisterRequest {

    @Schema(description = "Username for the new user", example = "john_doe")
    private String username;

    @Schema(description = "Email for the new user", example = "john@example.com")
    private String email;

    @Schema(description = "Password for the new user", example = "securePassword123")
    private String password;

    @Schema(description = "Full name of the user", example = "John Doe")
    private String fullName;

    @Schema(description = "User role", allowableValues = {"admin", "staff", "member"}, example = "staff")
    private String role;

    @Schema(description = "Store ID (required for staff role, must be null for admin role)", example = "1", nullable = true)
    private Integer storeId;
}
