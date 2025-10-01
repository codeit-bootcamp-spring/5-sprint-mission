package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.List;
import java.util.UUID;

public class InvalidParticipantException extends ChannelException {
    public InvalidParticipantException() {
        super(ErrorCode.INVALID_PARTICIPANT);
    }

    // null 참여자 ID가 포함된 경우
    public static InvalidParticipantException nullParticipant(int index, List<UUID> allParticipants) {
        InvalidParticipantException exception = new InvalidParticipantException();
        exception.addDetail("errorType", "NULL_PARTICIPANT");
        exception.addDetail("invalidIndex", index);
        exception.addDetail("allParticipants", allParticipants);
        return exception;
    }

    // 존재하지 않는 사용자 ID인 경우
    public static InvalidParticipantException invalidUser(UUID userId, List<UUID> allParticipants) {
        InvalidParticipantException exception = new InvalidParticipantException();
        exception.addDetail("errorType", "INVALID_USER");
        exception.addDetail("invalidUserId", userId);
        exception.addDetail("allParticipants", allParticipants);
        return exception;
    }

    // 중복 참여자가 있는 경우
    public static InvalidParticipantException duplicateUser(UUID duplicateUserId, List<UUID> allParticipants) {
        InvalidParticipantException exception = new InvalidParticipantException();
        exception.addDetail("errorType", "DUPLICATE_PARTICIPANT");
        exception.addDetail("duplicateUserId", duplicateUserId);
        exception.addDetail("allParticipants", allParticipants);
        return exception;
    }

    // 참여자 수가 부족한 경우
    public static InvalidParticipantException insufficientCount(int currentCount, int minRequired, List<UUID> allParticipants) {
        InvalidParticipantException exception = new InvalidParticipantException();
        exception.addDetail("errorType", "INSUFFICIENT_COUNT");
        exception.addDetail("currentCount", currentCount);
        exception.addDetail("minRequired", minRequired);
        exception.addDetail("allParticipants", allParticipants);
        return exception;
    }
}
