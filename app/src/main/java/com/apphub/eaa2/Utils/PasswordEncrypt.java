package com.apphub.eaa2.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncrypt {

    public static String encrypt(String input) {

        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            byte[] inputBytes = input.getBytes();
            byte[] hashBytes = sha256.digest(inputBytes);

            StringBuilder hexString = new StringBuilder();

            for (byte b : hashBytes) {

                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);

            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

            return null;
        }
    }

}
