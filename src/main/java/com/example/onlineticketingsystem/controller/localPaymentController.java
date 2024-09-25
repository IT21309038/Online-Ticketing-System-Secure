package com.example.onlineticketingsystem.controller;

import com.example.onlineticketingsystem.DTO.LocalPaymentDTO;
import com.example.onlineticketingsystem.entity.LocalPayment;
import com.example.onlineticketingsystem.service.AuditLogService;
import com.example.onlineticketingsystem.service.LocalPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/tickets")
@CrossOrigin
public class localPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(localPaymentController.class);

    @Autowired
    private LocalPaymentService localPaymentService;

    @Autowired
    private AuditLogService auditLogService;

    @PostMapping("/saveLocalPayment")
    public ResponseEntity<?> saveLocalPayment(@RequestBody LocalPaymentDTO localPaymentDTO, Authentication authentication) {
        logger.info("User '{}' is attempting to save a local payment", authentication.getName());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority ->
                grantedAuthority.getAuthority().equals("passenger") ||
                        grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized payment save attempt by user '{}'", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have passenger or admin role.");
        }

        try {
            LocalPaymentDTO localPayment = localPaymentService.saveLocalPayment(localPaymentDTO);
            auditLogService.createAuditLog("CREATE", authentication.getName(), authentication.getAuthorities().toString(),
                    "Saved local payment with payment ID " + localPaymentDTO.getPaymentID());

            logger.info("User '{}' successfully saved a local payment with payment id {}", authentication.getName(), localPaymentDTO.getPaymentID());
            return ResponseEntity.ok(localPayment);
        } catch (Exception e) {
            logger.error("An error occurred while saving local payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while saving local payment");
        }
    }

    @GetMapping("/getLocalPayment/{userID}")
    public ResponseEntity<?> getLocalPaymentByUserId(@PathVariable int userID, Authentication authentication) {
        logger.info("User '{}' is attempting to fetch local payments for user ID {}", authentication.getName(), userID);

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority ->
                grantedAuthority.getAuthority().equals("passenger") ||
                        grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized access attempt by user '{}' for user ID {}", authentication.getName(), userID);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have passenger or admin role.");
        }

        try {
            List<LocalPayment> localPayment = localPaymentService.getLocalPaymentByUserId(userID);
            auditLogService.createAuditLog("READ", authentication.getName(), authentication.getAuthorities().toString(),
                    "Fetched local payments for user ID " + userID);

            logger.info("User '{}' successfully fetched local payments for user ID {}", authentication.getName(), userID);
            return ResponseEntity.ok(localPayment);
        } catch (Exception e) {
            logger.error("An error occurred while fetching local payments for user ID {}: {}", userID, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching local payments");
        }
    }

    @GetMapping("/getLocalUniquePayment/{userID}/{refNumber}")
    public ResponseEntity<?> getLocalPaymentByUserIdAndRef(@PathVariable int userID, @PathVariable String refNumber, Authentication authentication) {
        logger.info("User '{}' is attempting to fetch a unique local payment for user ID {} and reference number {}", authentication.getName(), userID, refNumber);

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority ->
                grantedAuthority.getAuthority().equals("passenger") ||
                        grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized access attempt by user '{}' for user ID {} and reference number {}", authentication.getName(), userID, refNumber);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have passenger or admin role.");
        }

        try {
            List<LocalPayment> localPayment = localPaymentService.getLocalPaymentByUserIdAndRef(userID, refNumber);
            auditLogService.createAuditLog("READ", authentication.getName(), authentication.getAuthorities().toString(),
                    "Fetched local payment for user ID " + userID + " with reference number " + refNumber);

            logger.info("User '{}' successfully fetched a unique local payment for user ID {} and reference number {}", authentication.getName(), userID, refNumber);
            return ResponseEntity.ok(localPayment);
        } catch (Exception e) {
            logger.error("An error occurred while fetching a unique local payment for user ID {} and reference number {}: {}", userID, refNumber, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching the unique local payment");
        }
    }

}
