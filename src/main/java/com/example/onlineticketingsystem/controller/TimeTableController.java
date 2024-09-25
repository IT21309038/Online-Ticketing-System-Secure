package com.example.onlineticketingsystem.controller;

import com.example.onlineticketingsystem.DTO.TimeTableDTO;
import com.example.onlineticketingsystem.service.AuditLogService;
import com.example.onlineticketingsystem.service.TimeTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/timetable")
@CrossOrigin
public class TimeTableController {

    private static final Logger logger = LoggerFactory.getLogger(TimeTableController.class);

    @Autowired
    private TimeTableService timeTableService;

    @Autowired
    private AuditLogService auditLogService;

    // Save a timetable (admin only)
    @PostMapping("/saveTimetable")
    public ResponseEntity<?> saveTimetable(@RequestBody TimeTableDTO timeTableDTO, Authentication authentication) {
        logger.info("User '{}' is attempting to save a new timetable.", authentication.getName());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized attempt to save timetable by user '{}'.", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin role.");
        }

        try {
            TimeTableDTO timeTable = timeTableService.saveTimetable(timeTableDTO);
            auditLogService.createAuditLog("CREATE", authentication.getName(), authentication.getAuthorities().toString(),
                    "Created a new timetable: " + timeTableDTO);

            logger.info("User '{}' successfully saved a new timetable.", authentication.getName());
            return ResponseEntity.ok(timeTable);
        } catch (Exception e) {
            logger.error("An error occurred while saving the timetable: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while saving the timetable.");
        }
    }

    // Get all timetables (admin and ticket-inspector)
    @GetMapping("/getTimetable")
    public ResponseEntity<?> getTimetable(Authentication authentication) {
        logger.info("User '{}' is attempting to fetch all timetables.", authentication.getName());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin") || grantedAuthority.getAuthority().equals("ticket-inspector"))) {
            logger.warn("Unauthorized attempt to fetch timetables by user '{}'.", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin or ticket-inspector role.");
        }

        try {
            List<TimeTableDTO> timeTableDTOList = timeTableService.getAllTimetable();
            auditLogService.createAuditLog("READ", authentication.getName(), authentication.getAuthorities().toString(),
                    "Fetched all timetables.");

            logger.info("User '{}' successfully fetched all timetables.", authentication.getName());
            return ResponseEntity.ok(timeTableDTOList);
        } catch (Exception e) {
            logger.error("An error occurred while fetching timetables: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching timetables.");
        }
    }

    // Update a timetable (admin only)
    @PutMapping("/updateTimetable")
    public ResponseEntity<?> updateTimetable(@RequestBody TimeTableDTO timeTableDTO, Authentication authentication) {
        logger.info("User '{}' is attempting to update a timetable.", authentication.getName());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized attempt to update timetable by user '{}'.", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin role.");
        }

        try {
            TimeTableDTO updatedTimeTable = timeTableService.updateTimetable(timeTableDTO);
            auditLogService.createAuditLog("UPDATE", authentication.getName(), authentication.getAuthorities().toString(),
                    "Updated timetable: " + timeTableDTO);

            logger.info("User '{}' successfully updated a timetable.", authentication.getName());
            return ResponseEntity.ok(updatedTimeTable);
        } catch (Exception e) {
            logger.error("An error occurred while updating the timetable: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the timetable.");
        }
    }

    // Delete a timetable (admin only)
    @DeleteMapping("/deleteTimetable")
    public ResponseEntity<?> deleteTimetable(@RequestBody TimeTableDTO timeTableDTO, Authentication authentication) {
        logger.info("User '{}' is attempting to delete a timetable.", authentication.getName());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized attempt to delete timetable by user '{}'.", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin role.");
        }

        try {
            Boolean isDeleted = timeTableService.deleteTimetable(timeTableDTO);
            auditLogService.createAuditLog("DELETE", authentication.getName(), authentication.getAuthorities().toString(),
                    "Deleted timetable: " + timeTableDTO);

            logger.info("User '{}' successfully deleted a timetable.", authentication.getName());
            return ResponseEntity.ok(isDeleted);
        } catch (Exception e) {
            logger.error("An error occurred while deleting the timetable: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the timetable.");
        }
    }

}
