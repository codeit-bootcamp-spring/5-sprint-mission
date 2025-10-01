package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class BinaryContentStorageException extends BinaryContentException {

    public BinaryContentStorageException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static BinaryContentStorageException notFoundWithId(UUID binaryContentId) {
        BinaryContentStorageException exception = new BinaryContentStorageException(
            ErrorCode.FILE_NOT_FOUND);
        exception.addDetail("binaryContentId", binaryContentId);
        return exception;
    }

    public static BinaryContentStorageException alreadyExistsWithId(UUID binaryContentId) {
        BinaryContentStorageException exception = new BinaryContentStorageException(
            ErrorCode.DUPLICATE_FILE);
        exception.addDetail("binaryContentId", binaryContentId);
        return exception;
    }
}
