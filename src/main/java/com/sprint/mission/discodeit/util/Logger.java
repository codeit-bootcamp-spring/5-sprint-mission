package com.sprint.mission.discodeit.util;

public abstract class Logger {
    private static final int TAG_WIDTH = 25;

    public static void log(String tag, String message) {
        System.out.printf("[%-" + TAG_WIDTH + "s] %s%n", tag, message);
    }

    public static void logSection(String title) {
        System.out.println("=== " + title + " ===");
//        System.out.printf("[%-20s]%n", title);
    }
}
