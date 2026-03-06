package com.sms.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashUtil {
    public static String generateRouteHash(String boardingPoint, String destination) {
        try {
            String normalized = normalize(boardingPoint) + "|" + normalize(destination);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(normalized.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generating route hash", e);
        }
    }

    private static String normalize(String value) {
        return value == null
                ? ""
                : value.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
