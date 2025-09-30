package com.sprint.mission.discodeit.exception.user;


import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

// 도메인 에러
public class UserAlreadyExistsException extends UserException {
    public UserAlreadyExistsException() {
        super(ErrorCode.DUPLICATE_USER);
    }

    public static UserAlreadyExistsException withId(UUID userId) {
        UserAlreadyExistsException exception = new UserAlreadyExistsException();
        exception.addDetail("userId ", userId);
        return exception;
    }
    
    public static UserAlreadyExistsException withEmail(String email) {
        UserAlreadyExistsException exception = new UserAlreadyExistsException();
        exception.addDetail("email ", email);
        return exception;
    }
    
    public static UserAlreadyExistsException withUsername(String username) {
        UserAlreadyExistsException exception = new UserAlreadyExistsException();
        exception.addDetail("username ", username);
        return exception;
    }
} 