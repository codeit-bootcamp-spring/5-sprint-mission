package com.sprint.mission.discodeit.domain.binarycontent.exception;

import com.sprint.mission.discodeit.domain.common.exception.ErrorCode;

public class BinaryContentStorageException extends BinaryContentException {

    public BinaryContentStorageException(Throwable cause) {
        super(ErrorCode.BINARY_CONTENT_STORAGE_ERROR, cause);
    }
}
