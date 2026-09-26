package com.neuronix.auth;

import com.neuronix.auth.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }
    @PostMapping("/register")
    public AuthResponse register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return authService.register(request);
    }
    @PostMapping("/forget_password")
    public AuthResponse forgetPassword(
            @Valid @RequestBody ForgetPasswordRequest request
    ) {
        return authService.forgetPassword(request);
    }
    @PostMapping("/new_password")
    public void newPassword(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody NewPasswordRequest request
    ) {
        token = token.substring(7);

        authService.newPassword(token, request);
    }
}