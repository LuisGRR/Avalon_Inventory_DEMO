package com.avalon.Avalon_Inventory.domain.model.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class PasswordGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public static String generateRandomPassword(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
