package com.example.onlineticketingsystem.service;

import com.example.onlineticketingsystem.entity.AuditLog;
import com.example.onlineticketingsystem.repo.AuditLogRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepo auditLogRepo;

    public void createAuditLog(String action, String performedBy, String role, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setPerformedBy(performedBy);
        auditLog.setRole(role);
        auditLog.setTimestamp(Timestamp.from(Instant.now()));
        auditLog.setDetails(details);
        auditLogRepo.save(auditLog);
    }

    public List<AuditLog> getAllAuditLogs(){
        return auditLogRepo.findAll();
    }
}
