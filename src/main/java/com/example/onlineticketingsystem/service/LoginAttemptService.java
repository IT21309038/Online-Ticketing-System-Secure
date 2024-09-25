package com.example.onlineticketingsystem.security;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private final int MAX_ATTEMPT = 5;
    private final long BLOCK_TIME_MINS = 15;
    private Map<Integer, Integer> attemptsCache = new ConcurrentHashMap<>();
    private Map<Integer, LocalDateTime> blockCache = new ConcurrentHashMap<>();

    public void loginFailed(int userId) {
        int attempts = attemptsCache.getOrDefault(userId, 0);
        attempts++;
        attemptsCache.put(userId, attempts);

        if (attempts >= MAX_ATTEMPT) {
            blockCache.put(userId, LocalDateTime.now().plusMinutes(BLOCK_TIME_MINS));
        }
    }

    public void loginSucceeded(int userId) {
        attemptsCache.remove(userId);
        blockCache.remove(userId);
    }

    public boolean isBlocked(int userId) {
        if (blockCache.containsKey(userId)) {
            if (blockCache.get(userId).isAfter(LocalDateTime.now())) {
                return true;
            } else {
                blockCache.remove(userId); // Unblock after the time expires
            }
        }
        return false;
    }
}
