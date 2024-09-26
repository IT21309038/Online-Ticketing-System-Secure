package com.example.onlineticketingsystem.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RateLimiterService {
    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_DURATION = 10 * 60 * 1000; // 10 minutes

    // Stores the number of failed attempts for each IP address
    private Map<String, Integer> attempts = new HashMap<>();

    // Stores the block end time for each IP address
    private Map<String, Long> blockEndTime = new HashMap<>();

    // Check if the IP is blocked
    public boolean isBlocked(String ipAddress) {
        long currentTime = System.currentTimeMillis();

        if (blockEndTime.containsKey(ipAddress)) {
            long blockExpiresAt = blockEndTime.get(ipAddress);

            if (currentTime < blockExpiresAt) {
                // Still blocked
                return true;
            } else {
                // Unblock the IP after block duration has passed
                blockEndTime.remove(ipAddress);
                attempts.remove(ipAddress);
                return false;
            }
        }

        return false;
    }

    // Record a failed login attempt
    public void recordFailedAttempt(String ipAddress) {
        int currentAttempts = attempts.getOrDefault(ipAddress, 0);
        currentAttempts++;

        // If the number of failed attempts exceeds the max allowed, block the IP
        if (currentAttempts >= MAX_ATTEMPTS) {
            long blockUntil = System.currentTimeMillis() + BLOCK_DURATION;
            blockEndTime.put(ipAddress, blockUntil);
            attempts.put(ipAddress, MAX_ATTEMPTS);  // Keep track of blocked IP
        } else {
            attempts.put(ipAddress, currentAttempts);
        }
    }

    // Reset the attempt counter on successful login
    public void resetAttempts(String ipAddress) {
        attempts.remove(ipAddress);
        blockEndTime.remove(ipAddress);
    }

    // Retrieve the remaining attempts for a given IP address (useful for debugging)
    public int getRemainingAttempts(String ipAddress) {
        return MAX_ATTEMPTS - attempts.getOrDefault(ipAddress, 0);
    }
}
