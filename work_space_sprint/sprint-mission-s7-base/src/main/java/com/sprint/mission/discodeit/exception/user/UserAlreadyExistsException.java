package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserAlreadyExistsException extends UserException {
    
    public UserAlreadyExistsException(String email) {
        super(ErrorCode.DUPLICATE_USER, 
              "User with email " + email + " already exists", 
              Map.of("email", email));
    }
    
    public UserAlreadyExistsException(String field, String value) {
        super(ErrorCode.DUPLICATE_USER, 
              "User with " + field + " " + value + " already exists", 
              Map.of(field, value));
    }
}