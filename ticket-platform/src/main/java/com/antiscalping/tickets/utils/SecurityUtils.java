package com.antiscalping.tickets.utils;

import org.springframework.stereotype.Component;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

@Component
public class SecurityUtils {
    
    private static final SecureRandom secureRandom = new SecureRandom();
    
    public String generateTicketNumber(Long eventId, Long ticketId) {
        return String.format("TKT-%d-%d-%s", 
            eventId, 
            ticketId, 
            UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }
    
    public String generateQRSeed() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return bytesToHex(randomBytes);
    }
    
    public String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        return bytesToHex(hash);
    }
    
    public boolean verifyPassword(String password, String hash) throws NoSuchAlgorithmException {
        String hashedPassword = hashPassword(password);
        return hashedPassword.equals(hash);
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
