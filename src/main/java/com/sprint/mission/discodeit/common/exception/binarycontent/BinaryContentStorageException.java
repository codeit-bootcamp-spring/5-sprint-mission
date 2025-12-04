package com.sprint.mission.discodeit.common.exception.binarycontent;

import com.sprint.mission.discodeit.common.exception.ErrorCode;

public class BinaryContentStorageException extends BinaryContentException {

    public BinaryContentStorageException(Throwable cause) {
        super(ErrorCode.BINARY_CONTENT_STORAGE_ERROR, cause);
    }
}
