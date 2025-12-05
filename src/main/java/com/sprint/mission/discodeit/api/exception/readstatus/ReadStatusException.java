package com.sprint.mission.discodeit.api.exception.readstatus;

import com.sprint.mission.discodeit.api.exception.DiscodeitException;
import com.sprint.mission.discodeit.api.exception.ErrorCode;

import java.util.Map;

public class ReadStatusException extends DiscodeitException {

    public ReadStatusException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ReadStatusException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
