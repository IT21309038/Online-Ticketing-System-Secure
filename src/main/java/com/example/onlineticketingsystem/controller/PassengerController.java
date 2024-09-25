package com.example.onlineticketingsystem.controller;

import com.example.onlineticketingsystem.DTO.PassengerDTO;
import com.example.onlineticketingsystem.service.AuditLogService;
import com.example.onlineticketingsystem.service.PassengerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/tickets")
@CrossOrigin
public class PassengerController {

    private static final Logger logger = LoggerFactory.getLogger(PassengerController.class);

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private AuditLogService auditLogService;

    // Show all fields of passenger
    @GetMapping("/passengerById/{userID}")
    public ResponseEntity<?> getPassengerById(@PathVariable int userID, Authentication authentication) {
        logger.info("User '{}' is attempting to fetch passenger details for user ID {}", authentication.getName(), userID);

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority ->
                grantedAuthority.getAuthority().equals("passenger") || grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized access attempt by user '{}' for passenger ID {}", authentication.getName(), userID);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have passenger or admin role.");
        }

        try {
            PassengerDTO passengerDTO = passengerService.getPassengerById(userID);
            auditLogService.createAuditLog("READ", authentication.getName(), authentication.getAuthorities().toString(),
                    "Fetched passenger details for user ID " + userID);

            logger.info("User '{}' successfully fetched passenger details for user ID {}", authentication.getName(), userID);
            return new ResponseEntity<>(passengerDTO, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An error occurred while fetching passenger details for user ID {}: {}", userID, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching passenger details");
        }
    }

    // Show balance of passenger
    @GetMapping("/passengerBalance/{userID}")
    public ResponseEntity<?> getPassengerBalance(@PathVariable int userID, Authentication authentication) {
        logger.info("User '{}' is attempting to fetch balance for user ID {}", authentication.getName(), userID);

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority ->
                grantedAuthority.getAuthority().equals("passenger") || grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized access attempt by user '{}' for balance of user ID {}", authentication.getName(), userID);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have passenger or admin role.");
        }

        try {
            int balance = passengerService.getBalanceByUserId(userID);
            auditLogService.createAuditLog("READ", authentication.getName(), authentication.getAuthorities().toString(),
                    "Fetched balance for user ID " + userID);

            logger.info("User '{}' successfully fetched balance for user ID {}", authentication.getName(), userID);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            logger.error("An error occurred while fetching balance for user ID {}: {}", userID, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching balance");
        }
    }

    // Update balance
    @PutMapping("/updateBalance/{amount}/{userID}")
    public ResponseEntity<?> updateBalanceByUserId(@PathVariable int amount, @PathVariable int userID, Authentication authentication) {
        logger.info("User '{}' is attempting to update balance for user ID {} by amount {}", authentication.getName(), userID, amount);

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority ->
                grantedAuthority.getAuthority().equals("passenger") || grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized update attempt by user '{}' for user ID {}", authentication.getName(), userID);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have passenger or admin role.");
        }

        try {
            passengerService.updateBalanceByUserId(amount, userID);
            int updatedBalance = passengerService.getBalanceByUserId(userID);
            auditLogService.createAuditLog("UPDATE", authentication.getName(), authentication.getAuthorities().toString(),
                    "Updated balance by " + amount + " for user ID " + userID);

            logger.info("User '{}' successfully updated balance for user ID {} by amount {}", authentication.getName(), userID, amount);
            return ResponseEntity.ok(updatedBalance);
        } catch (Exception e) {
            logger.error("An error occurred while updating balance for user ID {}: {}", userID, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating balance");
        }
    }

}
