package com.example.onlineticketingsystem.controller;

import com.example.onlineticketingsystem.DTO.LocalPaymentDTO;
import com.example.onlineticketingsystem.DTO.OnlinePaymentDTO;
import com.example.onlineticketingsystem.entity.LocalPayment;
import com.example.onlineticketingsystem.entity.OnlinePayment;
import com.example.onlineticketingsystem.service.AuditLogService;
import com.example.onlineticketingsystem.service.LocalPaymentService;
import com.example.onlineticketingsystem.service.OnlinePaymentService;
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
public class OnlinePaymentController {
    private static final Logger logger = LoggerFactory.getLogger(OnlinePaymentController.class);

    @Autowired
    private OnlinePaymentService onlinePaymentService;

    @Autowired
    private AuditLogService auditLogService;

    @PostMapping("/saveOnlinePayment")
    public ResponseEntity<?> saveUser(@RequestBody OnlinePaymentDTO onlinePaymentDTO, Authentication authentication) {
        logger.info("User '{}' is attempting to save an online payment", authentication.getName());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("passenger"))) {
            logger.warn("Unauthorized payment save attempt by user '{}'", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have passenger role.");
        }

        OnlinePaymentDTO onlinePayment = onlinePaymentService.saveOnlinePayment(onlinePaymentDTO);
        auditLogService.createAuditLog("CREATE", authentication.getName(), authentication.getAuthorities().toString(),
                "Saved online payment with payment id " + onlinePaymentDTO.getPaymentID());

        logger.info("User '{}' successfully saved an online payment with payment id {}", authentication.getName(), onlinePaymentDTO.getPaymentID());
        return ResponseEntity.ok(onlinePayment);
    }

    @GetMapping("/getOnlinePayment/{userID}")
    public ResponseEntity<?> getOnlinePaymentByUserId(@PathVariable int userID, Authentication authentication) {
        logger.info("User '{}' is attempting to fetch online payments for user ID {}", authentication.getName(), userID);

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("passenger"))) {
            logger.warn("Unauthorized access attempt by user '{}' for user ID {}", authentication.getName(), userID);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have passenger role.");
        }

        List<OnlinePayment> onlinePayment = onlinePaymentService.getOnlinePaymentByUserId(userID);
        auditLogService.createAuditLog("READ", authentication.getName(), authentication.getAuthorities().toString(),
                "Fetched online payments for user ID " + userID);

        logger.info("User '{}' successfully fetched online payments for user ID {}", authentication.getName(), userID);
        return ResponseEntity.ok(onlinePayment);
    }
}
