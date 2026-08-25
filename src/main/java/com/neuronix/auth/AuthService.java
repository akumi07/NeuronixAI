package com.neuronix.auth;

import com.neuronix.auth.dto.AuthResponse;
import com.neuronix.auth.dto.LoginRequest;
import com.neuronix.auth.dto.RegisterRequest;
import com.neuronix.exception.InvalidCredentialsException;
import com.neuronix.security.JwtService;
import com.neuronix.user.UserService;
import com.neuronix.user.dto.CreateUserRequest;
import com.neuronix.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthResponse login(LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (BadCredentialsException exception) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(request.email());

        return new AuthResponse(token);
    }
    public AuthResponse register(RegisterRequest request) {

        UserResponse user = userService.createUser(
                new CreateUserRequest(
                        request.email(),
                        request.password()
                )
        );

        String token = jwtService.generateToken(user.email());

        return new AuthResponse(token);
    }
}