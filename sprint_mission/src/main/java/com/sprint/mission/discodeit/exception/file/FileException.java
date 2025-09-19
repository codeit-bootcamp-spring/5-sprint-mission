package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class FileException extends DiscodeitException {
    public FileException(ErrorCode errorCode) {
        super(errorCode);
    }
    public FileException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
} 