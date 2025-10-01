package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.UUID;

public class UnauthorizedMessageAccessException extends MessageException {
    public UnauthorizedMessageAccessException() {
        super(ErrorCode.UNAUTHORIZED_MESSAGE_ACCESS);
    }

    public static UnauthorizedMessageAccessException withDetails(UUID messageId, UUID requesterId, UUID authorId, String action) {
        UnauthorizedMessageAccessException exception = new UnauthorizedMessageAccessException();
        exception.addDetail("messageId", messageId);
        exception.addDetail("requesterId", requesterId);
        exception.addDetail("authorId", authorId);
        exception.addDetail("action", action);
        return exception;
    }
}
