package ru.taste.utilities.java;

public class TimerUtils {
    private long lastMillis;

    public static boolean delay(long lastMillis, long millis) {
        return System.currentTimeMillis() > lastMillis + millis;
    }

    public boolean delay(long millis) {
        return delay(lastMillis, millis);
    }

    public void reset() {
        lastMillis = System.currentTimeMillis();
    }
}
