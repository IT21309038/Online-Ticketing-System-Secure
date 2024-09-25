package com.example.onlineticketingsystem.controller;

import com.example.onlineticketingsystem.DTO.InspectDTO;
import com.example.onlineticketingsystem.entity.Inspect;
import com.example.onlineticketingsystem.service.AuditLogService;
import com.example.onlineticketingsystem.service.InspectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(value = "api/Inspect")
public class InspectController {

    private static final Logger logger = LoggerFactory.getLogger(InspectController.class);

    @Autowired
    private InspectService inspectService;

    @Autowired
    private AuditLogService auditLogService;

    @PostMapping("/createInspect")
    public ResponseEntity<?> saveInspect(@RequestBody InspectDTO inspectDTO, Authentication authentication) {
        logger.info("User '{}' is attempting to create an inspection", authentication.getName());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized access attempt by user '{}' without admin role", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin role.");
        }

        try {
        InspectDTO inspect = inspectService.saveInspect(inspectDTO);
        logger.info("User '{}' successfully created an inspection", authentication.getName());
        auditLogService.createAuditLog("Create", authentication.getName(), "admin", "Created an inspection");
        return ResponseEntity.ok(inspect);
        } catch (Exception e) {
            logger.error("An error occurred while creating an inspection: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating an inspection");
        }
    }

    @GetMapping("/getInspect")
    public ResponseEntity<?> getAllInspects(Authentication authentication) {
        logger.info("User '{}' is attempting to access all inspections", authentication.getName());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin") || grantedAuthority.getAuthority().equals("ticket-inspector"))) {
            logger.warn("Unauthorized access attempt by user '{}' without admin or ticket-inspector role", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin or ticket-inspector role.");
        }

        try{
        List<InspectDTO> inspects = inspectService.getAllInspects();
        if (inspects.isEmpty()) {
            logger.warn("No inspections found in the database");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No inspections found in the database");
        }
        logger.info("User '{}' successfully accessed inspection data", authentication.getName());
        auditLogService.createAuditLog("Read", authentication.getName(), "admin", "Accessed all inspections");
        return ResponseEntity.ok(inspects);
        } catch (Exception e) {
            logger.error("An error occurred while fetching inspection data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching inspection data");
        }
    }

    @GetMapping("/byInspector/{id}")
    public ResponseEntity<?> getInspectsByInspectorId(@PathVariable int id, Authentication authentication) {
        logger.info("User '{}' is attempting to access inspections by inspector ID {}", authentication.getName(), id);

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ticket-inspector") || grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized access attempt by user '{}' without ticket-inspector or admin role", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have ticket-inspector or admin role.");
        }

        try{
        List<Inspect> inspects = inspectService.getInspectsByInspectorId(id);

        if (inspects.isEmpty()) {
            logger.warn("No inspections found in the database for inspector ID {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No inspections found in the database for inspector ID " + id);
        }

        logger.info("User '{}' successfully accessed inspections for inspector ID {}", authentication.getName(), id);
        auditLogService.createAuditLog("Read", authentication.getName(), "ticket-inspector", "Accessed inspections for inspector ID " + id);
        return ResponseEntity.ok(inspects);
        } catch (Exception e) {
            logger.error("An error occurred while fetching inspection data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching inspection data");
        }
    }

    @DeleteMapping("/deleteInspect")
    public ResponseEntity<?> delete(@RequestBody InspectDTO inspectDTO, Authentication authentication) {
        logger.info("User '{}' is attempting to delete an inspection with ID {}", authentication.getName(), inspectDTO.getInspectId());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized deletion attempt by user '{}' without admin role", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin role.");
        }

        try {
        Boolean isDeleted = inspectService.delete(inspectDTO);
        logger.info("User '{}' successfully deleted an inspection with ID {}", authentication.getName(), inspectDTO.getInspectId());
        auditLogService.createAuditLog("Delete", authentication.getName(), "admin", "Deleted an inspection with ID " + inspectDTO.getInspectId());
        return ResponseEntity.ok(isDeleted);
        } catch (Exception e) {
            logger.error("An error occurred while deleting an inspection: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting an inspection");
        }
    }

    @PutMapping("/updateInspect")
    public ResponseEntity<?> updateInspect(@RequestBody InspectDTO inspectDTO, Authentication authentication) {
        logger.info("User '{}' is attempting to update an inspection with ID {}", authentication.getName(), inspectDTO.getInspectId());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized update attempt by user '{}' without admin role", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin role.");
        }

        try {
        InspectDTO updatedInspect = inspectService.updateInspect(inspectDTO);
        logger.info("User '{}' successfully updated an inspection with ID {}", authentication.getName(), inspectDTO.getInspectId());
        auditLogService.createAuditLog("Update", authentication.getName(), "admin", "Updated an inspection with ID " + inspectDTO.getInspectId());
        return ResponseEntity.ok(updatedInspect);
        } catch (Exception e) {
            logger.error("An error occurred while updating an inspection: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating an inspection");
        }
    }

    @GetMapping("/fraudCount")
    public ResponseEntity<?> fraudCount(Authentication authentication) {
        logger.info("User '{}' is attempting to access fraud count", authentication.getName());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin") || grantedAuthority.getAuthority().equals("ticket-inspector"))) {
            logger.warn("Unauthorized access attempt by user '{}' without admin or ticket-inspector role", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin or ticket-inspector role.");
        }

        try {
        int fraudCount = inspectService.fraudCount();
        logger.info("User '{}' successfully accessed fraud count", authentication.getName());
        return ResponseEntity.ok(fraudCount);
        } catch (Exception e) {
            logger.error("An error occurred while fetching fraud count: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching fraud count");
        }
    }

    @GetMapping("/fraudByRoute")
    public ResponseEntity<?> fraudByRoute(Authentication authentication) {
        logger.info("User '{}' is attempting to access fraud by route", authentication.getName());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin") || grantedAuthority.getAuthority().equals("ticket-inspector"))) {
            logger.warn("Unauthorized access attempt by user '{}' without admin or ticket-inspector role", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin or ticket-inspector role.");
        }

        try {
            Map<Integer, Integer> fraudByRoute = inspectService.fraudByRoute();
            if (fraudByRoute.isEmpty()) {
                logger.warn("No fraud data found in the database");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No fraud data found in the database");
            }
            logger.info("User '{}' successfully accessed fraud by route", authentication.getName());
            return ResponseEntity.ok(fraudByRoute);
        } catch (Exception e) {
            logger.error("An error occurred while fetching fraud by route: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching fraud by route");
        }
    }


}
