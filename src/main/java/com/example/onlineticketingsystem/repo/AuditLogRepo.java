package com.example.onlineticketingsystem.repo;

import com.example.onlineticketingsystem.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepo extends JpaRepository<AuditLog, Integer> {

}
