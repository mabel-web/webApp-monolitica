package com.usach.mytingeso.utils;

public final class RunUtils {

    private RunUtils() {}

    /** "12.345.678-5" or "123456785" -> "12345678-5" */
    public static String normalize(String run) {
        String clean = run.replace(".", "").replace(" ", "").replace("-", "").toUpperCase();
        if (clean.length() < 2) return clean;
        return clean.substring(0, clean.length() - 1) + "-" + clean.substring(clean.length() - 1);
    }

    /** Expects a normalized RUN. Validates format and check digit (módulo 11). */
    public static boolean isValid(String run) {
        if (!run.matches("^\\d{7,8}-[\\dK]$")) return false;

        String body = run.substring(0, run.length() - 2);
        char expected = run.charAt(run.length() - 1);

        int sum = 0, factor = 2;
        for (int i = body.length() - 1; i >= 0; i--) {
            sum += Character.getNumericValue(body.charAt(i)) * factor;
            factor = (factor == 7) ? 2 : factor + 1;
        }
        int result = 11 - (sum % 11);
        char dv = (result == 11) ? '0' : (result == 10) ? 'K' : (char) ('0' + result);

        return dv == expected;
    }
}