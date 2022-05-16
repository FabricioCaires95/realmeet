package br.com.sw2you.realmeet.utils;

import java.util.concurrent.TimeUnit;

public final class TestUtils {

    private TestUtils() {}

    public static void sleep(int millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
