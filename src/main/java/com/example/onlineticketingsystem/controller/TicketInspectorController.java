package com.example.onlineticketingsystem.controller;

import com.example.onlineticketingsystem.DTO.TicketInspectorDTO;
import com.example.onlineticketingsystem.entity.TicketInspector;
import com.example.onlineticketingsystem.service.AuditLogService;
import com.example.onlineticketingsystem.service.TicketInspectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/Inspector")
@CrossOrigin
public class TicketInspectorController {

    private static final Logger logger = LoggerFactory.getLogger(TicketInspectorController.class);

    @Autowired
    private TicketInspectorService ticketInspectorService;

    @Autowired
    private AuditLogService auditLogService;

    // Get all Inspectors (admin-only)
    @GetMapping("/getInspector")
    public ResponseEntity<?> getAllInspectors(Authentication authentication) {
        logger.info("User '{}' is attempting to fetch all ticket inspectors.", authentication.getName());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized attempt to fetch all inspectors by user '{}'.", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin role.");
        }

        List<TicketInspectorDTO> ticketInspector = ticketInspectorService.getAllInspectors();
        auditLogService.createAuditLog("READ", authentication.getName(), authentication.getAuthorities().toString(),
                "Fetched all ticket inspectors.");

        logger.info("User '{}' successfully fetched all ticket inspectors.", authentication.getName());
        return ResponseEntity.ok(ticketInspector);
    }

    // Get Inspector by ID (admin and ticket-inspector roles)
    @GetMapping("/byInspector/{id}")
    public ResponseEntity<?> getInspectorById(@PathVariable int id, Authentication authentication) {
        logger.info("User '{}' is attempting to fetch ticket inspector with ID {}.", authentication.getName(), id);

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin") || grantedAuthority.getAuthority().equals("ticket-inspector"))) {
            logger.warn("Unauthorized attempt to fetch inspector by ID {} by user '{}'.", id, authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin or ticket-inspector role.");
        }

        List<TicketInspector> ticketInspector = ticketInspectorService.getInspectorById(id);
        auditLogService.createAuditLog("READ", authentication.getName(), authentication.getAuthorities().toString(),
                "Fetched ticket inspector with ID: " + id);

        logger.info("User '{}' successfully fetched ticket inspector with ID {}.", authentication.getName(), id);
        return ResponseEntity.ok(ticketInspector);
    }


}
