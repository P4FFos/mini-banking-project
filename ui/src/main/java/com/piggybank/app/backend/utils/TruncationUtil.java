package com.piggybank.app.backend.utils;

public class TruncationUtil {
    public static double truncate(double value) {
        return Math.floor(value * 100) / 100;
    }
}