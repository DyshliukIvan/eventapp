package com.eventapp.backend.auth;

import com.eventapp.backend.models.User;
import com.eventapp.backend.repositories.UserRepository;
import com.eventapp.backend.security.GoogleTokenVerifierService;
import com.eventapp.backend.security.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final GoogleTokenVerifierService googleTokenVerifierService;

    public AuthController(
            UserRepository userRepository,
            JwtService jwtService,
            GoogleTokenVerifierService googleTokenVerifierService
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.googleTokenVerifierService = googleTokenVerifierService;
    }

    @PostMapping("/fake-login")
    public AuthResponse fakeLogin(@RequestBody FakeLoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalStateException("Email is required");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(request.getEmail());
                    newUser.setName(request.getName());
                    newUser.setProvider("local");
                    newUser.setProviderUserId(request.getEmail());
                    newUser.setEmailVerified(true);
                    return newUser;
                });

        user.setName(request.getName());
        user.setLastLoginAt(Instant.now());

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(
                token,
                "Bearer",
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                "Login successful"
        );
    }

    @PostMapping("/google")
    public AuthResponse googleLogin(@RequestBody GoogleLoginRequest request) {
        if (request.getIdToken() == null || request.getIdToken().isBlank()) {
            throw new IllegalStateException("Google ID token is required");
        }

        GoogleIdToken.Payload payload = googleTokenVerifierService.verify(request.getIdToken());

        String provider = "google";
        String providerUserId = payload.getSubject(); // sub
        String email = payload.getEmail();
        Boolean emailVerified = payload.getEmailVerified();
        String name = (String) payload.get("name");

        User user = userRepository
                .findByProviderAndProviderUserId(provider, providerUserId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setProvider(provider);
                    newUser.setProviderUserId(providerUserId);
                    newUser.setRole("ROLE_USER");
                    return newUser;
                });

        user.setEmail(email);
        user.setEmailVerified(Boolean.TRUE.equals(emailVerified));
        user.setName(name);
        user.setLastLoginAt(Instant.now());

        User savedUser = userRepository.save(user);

        String appJwt = jwtService.generateToken(savedUser);

        return new AuthResponse(
                appJwt,
                "Bearer",
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                "Google login successful"
        );
    }

    @GetMapping("/me")
    public CurrentUserResponse me(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalStateException("Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(7);
        Long userId = jwtService.extractUserId(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        return new CurrentUserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
    }
}