package com.example.onlineticketingsystem.controller;

import com.example.onlineticketingsystem.DTO.AuthResponseDTO;
import com.example.onlineticketingsystem.DTO.LoginDTO;
import com.example.onlineticketingsystem.DTO.RegisterDTO;
import com.example.onlineticketingsystem.service.AuthService;
import com.example.onlineticketingsystem.service.RateLimiterService;
import com.example.onlineticketingsystem.service.SecurityLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final SecurityLogService securityLogService;
    private final RateLimiterService rateLimiterService;

    @Autowired
    public AuthController(AuthService authService, SecurityLogService securityLogService, RateLimiterService rateLimiterService) {
        this.authService = authService;
        this.securityLogService = securityLogService;
        this.rateLimiterService = rateLimiterService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        try {
            authService.registerUser(registerDTO);
            logger.info("User '{}' registered successfully from IP: {}", registerDTO.getName(), ipAddress);
            securityLogService.logAccess("REGISTER", registerDTO.getName(), "SUCCESS", ipAddress, "User registration successful");
            return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.warn("Failed registration attempt by '{}' from IP: {}", registerDTO.getName(), ipAddress);
            securityLogService.logAccess("REGISTER", registerDTO.getName(), "FAILURE", ipAddress, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();

        // Log remaining attempts before checking if blocked
        int remainingAttempts = rateLimiterService.getRemainingAttempts(ipAddress);
        logger.info("Remaining login attempts for IP {}: {}", ipAddress, remainingAttempts);

        // Check if the IP is currently blocked due to brute force attempts
        if (rateLimiterService.isBlocked(ipAddress)) {
            logger.warn("Blocked login attempt from blocked IP: {}", ipAddress);
            securityLogService.logAccess("LOGIN", loginDTO.getUsername(), "BLOCKED", ipAddress, "IP blocked due to multiple failed attempts");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many failed attempts. Please try again later.");
        }

        try {
            AuthResponseDTO response = authService.loginUser(loginDTO);
            logger.info("User '{}' logged in successfully from IP: {}", loginDTO.getUsername(), ipAddress);
            securityLogService.logAccess("LOGIN", loginDTO.getUsername(), "SUCCESS", ipAddress, "User login successful");

            // Reset attempts on successful login
            rateLimiterService.resetAttempts(ipAddress);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Log failed login attempt
            logger.warn("Failed login attempt by '{}' from IP: {}. Reason: {}", loginDTO.getUsername(), ipAddress, e.getMessage());

            // Record the failed attempt
            rateLimiterService.recordFailedAttempt(ipAddress);

            // Log remaining attempts after failure
            remainingAttempts = rateLimiterService.getRemainingAttempts(ipAddress);
            logger.info("Remaining login attempts after failure for IP {}: {}", ipAddress, remainingAttempts);

            securityLogService.logAccess("LOGIN", loginDTO.getUsername(), "FAILURE", ipAddress, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed. Please check your credentials.");
        }
    }
}
