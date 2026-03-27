package com.axia.inventorymanagment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private String role;
    private String fullName;
    private Integer storeId;
}
