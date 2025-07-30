package com.sprint.mission.discodeit.exception;

public class GlobalExceptionHandler {
    public static void handleException(Exception e){


        if(e instanceof IllegalArgumentException ){
            System.out.println("IllegalArgumentException 발생");
        }else{
            System.out.println("other exception");
        }
        System.out.println(e.getMessage());
        e.printStackTrace();
    }
}
