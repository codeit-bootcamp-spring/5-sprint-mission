package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class UserNotFoundException extends UserException {
    
    public UserNotFoundException(UUID userId) {
        super(ErrorCode.USER_NOT_FOUND, 
              "User with id " + userId + " not found", 
              Map.of("userId", userId));
    }
    
    public UserNotFoundException(String username) {
        super(ErrorCode.USER_NOT_FOUND, 
              "User with username " + username + " not found", 
              Map.of("username", username));
    }
}