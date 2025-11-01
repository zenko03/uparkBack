package com.urban.upark.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String name;
    private String first_name;
    private String user_name;
    private String email;
    private String password;
    private String phone_number;
    private String role;
}