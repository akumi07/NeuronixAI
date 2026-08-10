package com.neuronix.user.dto;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        LocalDateTime createdAt
) {
}