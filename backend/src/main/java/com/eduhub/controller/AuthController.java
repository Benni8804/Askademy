package com.eduhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduhub.dto.AuthResponse;
import com.eduhub.dto.LoginRequest;
import com.eduhub.dto.RegisterRequest;
import com.eduhub.model.Role;
import com.eduhub.model.User;
import com.eduhub.security.JwtUtil;
import com.eduhub.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            System.out.println(">>> LOGIN REQUEST RECEIVED: " + request.getEmail());
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userService.findByEmail(request.getEmail()).orElseThrow();
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
            AuthResponse response = new AuthResponse(token, user.getId(), user.getEmail(), user.getRole().name());

            System.out.println(">>> LOGIN SUCCESSFUL for: " + user.getEmail());
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.core.AuthenticationException e) {
            System.err.println(">>> LOGIN ERROR: " + e.getMessage());
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            System.out.println(">>> REGISTER REQUEST RECEIVED: " + request.getEmail() + " - " + request.getRole());
            
            if (userService.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body("Email already exists");
            }

            User user = new User();
            user.setFirstname(request.getFirstName() != null ? request.getFirstName() : "");
            user.setLastname(request.getLastName() != null ? request.getLastName() : "");
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setRole(Role.valueOf(request.getRole().toUpperCase()));

            user = userService.registerUser(user);
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
            AuthResponse response = new AuthResponse(token, user.getId(), user.getEmail(), user.getRole().name());

            System.out.println(">>> REGISTRATION SUCCESSFUL for: " + user.getEmail());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            System.err.println(">>> REGISTRATION ERROR: Invalid role - " + e.getMessage());
            return ResponseEntity.badRequest().body("Registration failed: Invalid role");
        } catch (RuntimeException e) {
            System.err.println(">>> REGISTRATION ERROR: " + e.getMessage());
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }
}