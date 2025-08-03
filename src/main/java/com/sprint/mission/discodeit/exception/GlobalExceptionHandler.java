package com.sprint.mission.discodeit.exception;

import java.io.IOException;

public class GlobalExceptionHandler {

    public static void handle(Exception e) {
        if (e instanceof IOException) {
            System.out.println("IO Exception 발생: " + e.getMessage());
            e.printStackTrace();

        } else if (e instanceof ClassNotFoundException) {
            System.out.println("ClassNotFoundException 발생: " + e.getMessage());
            e.printStackTrace();

        } else {
            System.out.println("알 수 없는 예외 발생: " + e.getMessage());
            e.printStackTrace();

        }
    }
}
