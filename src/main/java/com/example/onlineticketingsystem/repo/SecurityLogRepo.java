package com.example.onlineticketingsystem.repo;

import com.example.onlineticketingsystem.entity.SecurityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityLogRepo extends JpaRepository<SecurityLog, Integer> {

}
