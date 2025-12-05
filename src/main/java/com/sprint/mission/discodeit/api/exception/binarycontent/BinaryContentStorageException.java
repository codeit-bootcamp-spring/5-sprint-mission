package com.sprint.mission.discodeit.api.exception.binarycontent;

import com.sprint.mission.discodeit.api.exception.ErrorCode;

public class BinaryContentStorageException extends BinaryContentException {

    public BinaryContentStorageException(Throwable cause) {
        super(ErrorCode.BINARY_CONTENT_STORAGE_ERROR, cause);
    }
}
