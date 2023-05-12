package com.apphub.eaa2.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncrypt {

    public static String encrypt(String input) {
        try {
            // Get an instance of the SHA-256 algorithm
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            // Convert the input string to bytes and apply the SHA-256 algorithm
            byte[] inputBytes = input.getBytes();
            byte[] hashBytes = sha256.digest(inputBytes);

            // Convert the resulting hash bytes to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle the case where the SHA-256 algorithm is not available
            e.printStackTrace();
            return null;
        }
    }

}
