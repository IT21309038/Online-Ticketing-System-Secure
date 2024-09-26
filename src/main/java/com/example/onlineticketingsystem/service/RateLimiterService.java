package com.example.onlineticketingsystem.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RateLimiterService {
    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_DURATION = 10 * 60 * 1000; // 10 minutes

    private Map<String, Integer> attempts = new HashMap<>();
    private Map<String, Long> blockEndTime = new HashMap<>();

    // Check if the IP is blocked
    public boolean isBlocked(String ipAddress) {
        if (!blockEndTime.containsKey(ipAddress)) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if (blockEndTime.get(ipAddress) > currentTime) {
            return true;
        }

        // Unblock if the block duration has passed
        blockEndTime.remove(ipAddress);
        attempts.remove(ipAddress);
        return false;
    }

    // Record a failed login attempt
    public void recordFailedAttempt(String ipAddress) {
        int currentAttempts = attempts.getOrDefault(ipAddress, 0);
        currentAttempts++;
        attempts.put(ipAddress, currentAttempts);

        if (currentAttempts >= MAX_ATTEMPTS) {
            blockEndTime.put(ipAddress, System.currentTimeMillis() + BLOCK_DURATION);
            attempts.remove(ipAddress);
        }
    }

    // Reset the attempt counter on successful login
    public void resetAttempts(String ipAddress) {
        attempts.remove(ipAddress);
        blockEndTime.remove(ipAddress);
    }
}
