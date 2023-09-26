package ru.taste.utilities.java;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringUtils {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTVWXYZ0123456789";
    private static final String DIGITS = "0123456789";

    public static String random(int length) {
        return IntStream.range(0, length).mapToObj(i -> String.valueOf(ALPHABET.toCharArray()[ThreadLocalRandom.current().nextInt(0, ALPHABET.length())])).collect(Collectors.joining());
    }

    public static String randomDigit(int length) {
        return IntStream.range(0, length).mapToObj(i -> String.valueOf(DIGITS.toCharArray()[ThreadLocalRandom.current().nextInt(0, DIGITS.length())])).collect(Collectors.joining());
    }

    public static String randomCode() {
        return String.format("%s-%s-%s", random(5), random(5), random(5)).toUpperCase();
    }

    public static boolean invalidUrl(String string) {
        return !string.matches("^(http|https|ftp)://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}(/.*)?$");
    }

    public static boolean invalidImageUrl(String string) {
        return !string.matches(".*\\b(https?://\\S+\\.(jpg|jpeg|png|gif|bmp))\\b.*");
    }

    public static boolean containsSpecialSymbols(String string) {
        return string.matches(".*[^a-zA-Z0-9].*");
    }

    public static boolean startWithNumber(String string) {
        for (char c : "0123456789".toCharArray()) {
            return string.startsWith(String.valueOf(c));
        }

        return false;
    }

    public static String durationString(long milliseconds) {
        if (milliseconds == -1) {
            return "Lifetime";
        }

        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long weeks = days / 7;
        long months = days / 30;
        long threeMonths = days / 90;
        long halfYear = days / 180;
        long years = days / 365;

        if (years > 0) {
            return "Year";
        } else if (halfYear > 0) {
            return "Half a Year";
        } else if (threeMonths > 0) {
            return "Three Months";
        } else if (months > 0) {
            return "Month";
        } else if (weeks > 0) {
            return "Week";
        } else if (days > 0) {
            return "Day";
        } else {
            return "No Limit";
        }
    }

    public static String timestamp(String format, long from, long to) {
        if (to == -1) {
            return "Never";
        }

        return new SimpleDateFormat(format, Locale.ENGLISH).format(new Date(from + to));
    }

    public static String timestamp(String format, long to) {
        return timestamp(format, 0, to);
    }

    public static String timestamp(long from, long to) {
        return timestamp("dd MMMM yyyy", from, to);
    }

    public static String timestamp(long to) {
        return timestamp(0, to);
    }
}
