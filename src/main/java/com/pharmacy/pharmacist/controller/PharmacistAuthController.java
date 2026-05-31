package com.pharmacy.pharmacist.controller;

import com.pharmacy.shared.entity.User;
import com.pharmacy.shared.repository.PharmacyRepository;
import com.pharmacy.shared.repository.UserRepository;
import com.pharmacy.shared.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/pharmacist/auth")
@Tag(name = "Pharmacist Auth", description = "Pharmacist authentication endpoints")
public class PharmacistAuthController {

    private final UserRepository userRepository;
    private final PharmacyRepository pharmacyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public PharmacistAuthController(UserRepository userRepository,
                                    PharmacyRepository pharmacyRepository,
                                    PasswordEncoder passwordEncoder,
                                    JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Operation(summary = "Pharmacist login with pharmacy context")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid PharmacistLoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())
                || !"ROLE_PHARMACIST".equals(user.getRole())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid pharmacist credentials"));
        }

        if (request.pharmacyId() == null || pharmacyRepository.findById(request.pharmacyId()).isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "Invalid pharmacyId"));
        }

        String token = jwtUtil.generateToken(user, request.pharmacyId());
        return ResponseEntity.ok(Map.of("token", token));
    }

    public record PharmacistLoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password,
            @NotNull Long pharmacyId
    ) {}
}
