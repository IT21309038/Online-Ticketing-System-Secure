package com.example.onlineticketingsystem.controller;

import com.example.onlineticketingsystem.DTO.RouteDTO;
import com.example.onlineticketingsystem.service.AuditLogService;
import com.example.onlineticketingsystem.service.RouteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/route")
@CrossOrigin
public class RouteController {
    private static final Logger logger = LoggerFactory.getLogger(RouteController.class);

    @Autowired
    private RouteService routeService;

    @Autowired
    private AuditLogService auditLogService;

    // Save Route
    @PostMapping("/saveRoute")
    public ResponseEntity<?> saveRoute(@RequestBody RouteDTO routeDTO, Authentication authentication) {
        logger.info("User '{}' is attempting to save a new route.", authentication.getName());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized save route attempt by user '{}'", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin role.");
        }

        RouteDTO route = routeService.saveRoute(routeDTO);
        auditLogService.createAuditLog("CREATE", authentication.getName(), authentication.getAuthorities().toString(),
                "Created route with details: " + routeDTO.toString());

        logger.info("User '{}' successfully saved a new route.", authentication.getName());
        return ResponseEntity.ok(route);
    }

    // Get All Routes
    @GetMapping("/getRoute")
    public ResponseEntity<?> getRoute(Authentication authentication) {
        logger.info("User '{}' is attempting to fetch all routes.", authentication.getName());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized get route attempt by user '{}'", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin role.");
        }

        List<RouteDTO> routes = routeService.getAllRoutes();
        auditLogService.createAuditLog("READ", authentication.getName(), authentication.getAuthorities().toString(),
                "Fetched all routes.");

        logger.info("User '{}' successfully fetched all routes.", authentication.getName());
        return ResponseEntity.ok(routes);
    }

    // Update Route
    @PutMapping("/updateRoute")
    public ResponseEntity<?> updateRoute(@RequestBody RouteDTO routeDTO, Authentication authentication) {
        logger.info("User '{}' is attempting to update route number {}.", authentication.getName(), routeDTO.getRouteNo());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized update route attempt by user '{}'", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin role.");
        }

        RouteDTO route = routeService.updateRoute(routeDTO);
        auditLogService.createAuditLog("UPDATE", authentication.getName(), authentication.getAuthorities().toString(),
                "Updated route number: " + routeDTO.getRouteNo());

        logger.info("User '{}' successfully updated route number {}.", authentication.getName(), routeDTO.getRouteNo());
        return ResponseEntity.ok(route);
    }

    // Delete Route
    @DeleteMapping("/deleteRoute")
    public ResponseEntity<?> deleteRoute(@RequestBody RouteDTO routeDTO, Authentication authentication) {
        logger.info("User '{}' is attempting to delete route number {}.", authentication.getName(), routeDTO.getRouteNo());

        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"))) {
            logger.warn("Unauthorized delete route attempt by user '{}'", authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied. You need to have admin role.");
        }

        Boolean isDeleted = routeService.deleteRoute(routeDTO);
        auditLogService.createAuditLog("DELETE", authentication.getName(), authentication.getAuthorities().toString(),
                "Deleted route number: " + routeDTO.getRouteNo());

        logger.info("User '{}' successfully deleted route with ID {}.", authentication.getName(), routeDTO.getRouteNo());
        return ResponseEntity.ok(isDeleted);
    }
}