package com.sprint.mission.discodeit.domain.binarycontent.domain.exception;

import com.sprint.mission.discodeit.global.error.ErrorCode;

public class BinaryContentStorageException extends BinaryContentException {

    public BinaryContentStorageException(Throwable cause) {
        super(ErrorCode.BINARY_CONTENT_STORAGE_ERROR, cause);
    }
}
