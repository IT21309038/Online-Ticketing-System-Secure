package com.example.onlineticketingsystem.controller;

import com.example.onlineticketingsystem.entity.AuditLog;
import com.example.onlineticketingsystem.entity.SecurityLog;
import com.example.onlineticketingsystem.service.AuditLogService;
import com.example.onlineticketingsystem.service.SecurityLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/logs")
@CrossOrigin
public class SecurityAuditController {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAuditController.class);

    private final SecurityLogService securityLogService;
    private final AuditLogService auditLogService;

    @Autowired
    public SecurityAuditController(SecurityLogService securityLogService, AuditLogService auditLogService) {
        this.securityLogService = securityLogService;
        this.auditLogService = auditLogService;
    }

    // Get all Security Logs
    @GetMapping("/security-logs")
    public ResponseEntity<?> getAllSecurityLogs(Authentication authentication) {
        // Check if the user is an admin
        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized attempt to access Security Logs by '{}'", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Admin role required.");
        }

        List<SecurityLog> securityLogs = securityLogService.getAllSecurityLogs();
        logger.info("Admin '{}' accessed Security Logs", authentication.getName());
        return ResponseEntity.ok(securityLogs);
    }

    // Get all Audit Logs
    @GetMapping("/audit-logs")
    public ResponseEntity<?> getAllAuditLogs(Authentication authentication) {
        // Check if the user is an admin
        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized attempt to access Audit Logs by '{}'", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. Admin role required.");
        }

        List<AuditLog> auditLogs = auditLogService.getAllAuditLogs();
        logger.info("Admin '{}' accessed Audit Logs", authentication.getName());
        return ResponseEntity.ok(auditLogs);
    }
}