package com.urban.upark.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la vérification du code de réinitialisation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyResetCodeRequest {
    private String email;
    private String code;
}
