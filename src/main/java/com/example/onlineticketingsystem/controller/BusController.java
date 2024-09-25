package com.example.onlineticketingsystem.controller;

import com.example.onlineticketingsystem.DTO.BusDTO;
import com.example.onlineticketingsystem.service.AuditLogService;
import com.example.onlineticketingsystem.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/bus")
@CrossOrigin
public class BusController {

    private static final Logger logger = LoggerFactory.getLogger(BusController.class);

    @Autowired
    private BusService busService;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/getBus")
    public ResponseEntity<?> getBus(Authentication authentication){
        logger.info("User '{}' is attempting to access all buses", authentication.getName());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"))){
            logger.warn("Unauthorized access attempt by user '{}' without admin role", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin role.");
        }

        try {
            List<BusDTO> busDTOList = busService.getAllBuses();

            if (busDTOList.isEmpty()){
                logger.warn("No buses found in the database");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No buses found in the database");
            }else {
                logger.info("User '{}' successfully accessed bus data", authentication.getName());
                auditLogService.createAuditLog("Read", authentication.getName(), getRole(authentication), "Accessed all buses");
            }
            return ResponseEntity.ok(busDTOList);
        }
        catch (Exception e){
            logger.error("An error occurred while fetching bus data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching bus data");
        }
    }

    @PutMapping("/updateBus")
    public ResponseEntity<?> updateBus(@RequestBody BusDTO busDTO, Authentication authentication){
        logger.info("User '{}' is attempting to update bus with ID {}", authentication.getName(), busDTO.getId());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin") || grantedAuthority.getAuthority().equals("bus-owner"))){
            logger.warn("Unauthorized update attempt by user '{}' without admin or bus-owner role", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin or bus-owner role.");
        }

        try {
        BusDTO updatedBus = busService.updateBus(busDTO);
        logger.info("User '{}' successfully updated bus with ID {}", authentication.getName(), busDTO.getId());
        auditLogService.createAuditLog("Update", authentication.getName(), getRole(authentication), "Updated bus with ID " + busDTO.getId());
        return ResponseEntity.ok(updatedBus);
        }
        catch (Exception e){
            logger.error("An error occurred while updating bus data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating bus data");
        }
    }

    @DeleteMapping("/deleteBus")
    public ResponseEntity<?> deleteBus(@RequestBody BusDTO busDTO, Authentication authentication){
        logger.info("User '{}' is attempting to delete bus with ID {}", authentication.getName(), busDTO.getId());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin") || grantedAuthority.getAuthority().equals("bus-owner"))){
            logger.warn("Unauthorized delete attempt by user '{}' without admin or bus-owner role", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin or bus-owner role.");
        }

        try{
        Boolean isDeleted = busService.deleteBus(busDTO);
        logger.info("User '{}' successfully deleted bus with ID {}", authentication.getName(), busDTO.getId());
        auditLogService.createAuditLog("Delete", authentication.getName(), getRole(authentication), "Deleted bus with ID " + busDTO.getId());
        return ResponseEntity.ok(isDeleted);
        }
        catch (Exception e){
            logger.error("An error occurred while deleting bus data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting bus data");
        }
    }

    @PostMapping("/saveBus")
    public ResponseEntity<?> saveBus(@RequestBody BusDTO busDTO, Authentication authentication){
        logger.info("User '{}' is attempting to save a new bus", authentication.getName());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin") || grantedAuthority.getAuthority().equals("bus-owner"))){
            logger.warn("Unauthorized bus creation attempt by user '{}' without admin or bus-owner role", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin or bus-owner role.");
        }

        try {
            BusDTO bus = busService.saveBus(busDTO);
            logger.info("User '{}' successfully created a new bus with ID {}", authentication.getName(), bus.getId());
            auditLogService.createAuditLog("Create", authentication.getName(), getRole(authentication), "Created bus with ID " + bus.getId());
            return ResponseEntity.ok(bus);
        } catch (Exception e) {
            logger.error("An error occurred while saving bus data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while saving bus data");
        }
    }

    private String getRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .findFirst()
                .orElse("ROLE_UNKNOWN");
    }
}
