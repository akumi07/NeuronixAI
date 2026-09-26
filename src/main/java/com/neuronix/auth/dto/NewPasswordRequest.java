package com.neuronix.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record NewPasswordRequest(
        @NotBlank(message = "Password is required")
        String password
) {
}