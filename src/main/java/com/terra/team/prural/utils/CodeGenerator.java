package com.terra.team.prural.utils;

import java.security.SecureRandom;
import java.util.UUID;

public class CodeGenerator {

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    /**
     * Genera un token único usando UUID.
     */
    public static String generateUUIDToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Genera un código numérico aleatorio (ej: para 2FA o verificación).
     * @param length cantidad de dígitos
     */
    public static String generateNumericCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // solo números 0-9
        }
        return sb.toString();
    }

    /**
     * Genera un código alfanumérico aleatorio.
     * @param length cantidad de caracteres
     */
    public static String generateAlphanumericCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(index));
        }
        return sb.toString();
    }
}
