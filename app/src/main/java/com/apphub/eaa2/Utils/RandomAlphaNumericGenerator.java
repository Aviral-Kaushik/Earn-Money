package com.apphub.eaa2.Utils;

import java.util.Random;

public class RandomAlphaNumericGenerator {

    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final char[] ALPHANUMERIC = (LETTERS + LETTERS.toUpperCase() + "0123456789").toCharArray();
    public static String generateAlphaNumeric(int length){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i =0; i<length; i++){
            stringBuilder.append(ALPHANUMERIC[new Random().nextInt(ALPHANUMERIC.length)]);
        }
        return stringBuilder.toString();
    }

    public static String generateOTP(int length) {
        String digits = "0123456789";
        Random random = new Random();
        StringBuilder otp = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(digits.length());
            otp.append(digits.charAt(index));
        }

        return otp.toString();
    }

}
