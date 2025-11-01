package com.urban.upark.dto;

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
    private String phone_number;
    private String password;
    private String role;

}
