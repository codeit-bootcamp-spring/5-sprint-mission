package com.sprint.mission.discodeit.binarycontent.domain.exception;

import com.sprint.mission.discodeit.common.domain.exception.ErrorCode;

public class BinaryContentStorageException extends BinaryContentException {

    public BinaryContentStorageException(Throwable cause) {
        super(ErrorCode.BINARY_CONTENT_STORAGE_ERROR, cause);
    }
}
