package com.sprint.mission.discodeit.exception;

import java.io.IOException;
import java.util.NoSuchElementException;

public class GlobalExceptionHandler {
    public static void handleException(Exception e){
        if(e instanceof IllegalArgumentException ){
            System.out.println("IllegalArgumentException 발생");
        } else if(e instanceof IOException){
            System.out.println("IOException 발생");
        } else if(e instanceof NoSuchElementException){
            System.out.println("NoSuchElementException 발생");
        }
         else{
            System.out.println("other exception");
        }
        System.out.println(e.getMessage());
        e.printStackTrace();
    }
}
