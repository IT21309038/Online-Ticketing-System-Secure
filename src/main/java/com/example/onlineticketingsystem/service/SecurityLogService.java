package com.example.onlineticketingsystem.service;

import com.example.onlineticketingsystem.DTO.BusDTO;
import com.example.onlineticketingsystem.entity.Bus;
import com.example.onlineticketingsystem.entity.SecurityLog;
import com.example.onlineticketingsystem.repo.SecurityLogRepo;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class SecurityLogService {

    @Autowired
    private SecurityLogRepo securityLogRepo;

    public void logAccess(String action, String username, String status, String ipAddress, String details){
        SecurityLog log = new SecurityLog();
        log.setAction(action);
        log.setUsername(username);
        log.setStatus(status);
        log.setIpAddress(ipAddress);
        log.setTimestamp(Timestamp.from(Instant.now()));
        log.setDetails(details);
        securityLogRepo.save(log);
    }

    public List<SecurityLog> getAllSecurityLogs(){
        return securityLogRepo.findAll();
    }




}
