package com.example.assistant;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Utils {
    public static String timeNow() {
        LocalTime t = LocalTime.now();
        return t.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    public static double safeParseDouble(String s, double fallback) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return fallback;
        }
    }
}