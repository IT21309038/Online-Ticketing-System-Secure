package com.example.onlineticketingsystem.controller;

import com.example.onlineticketingsystem.DTO.AuthResponseDTO;
import com.example.onlineticketingsystem.DTO.LoginDTO;
import com.example.onlineticketingsystem.DTO.RegisterDTO;
import com.example.onlineticketingsystem.service.AuthService;
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

    @Autowired
    private SecurityLogService securityLogService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Register new user
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

    // User login
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        try {
            AuthResponseDTO response = authService.loginUser(loginDTO);
            logger.info("User '{}' logged in successfully from IP: {}", loginDTO.getUsername(), ipAddress);
            securityLogService.logAccess("LOGIN", loginDTO.getUsername(), "SUCCESS", ipAddress, "User login successful");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.warn("Failed login attempt by '{}' from IP: {}", loginDTO.getUsername(), ipAddress);
            securityLogService.logAccess("LOGIN", loginDTO.getUsername(), "FAILURE", ipAddress, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
