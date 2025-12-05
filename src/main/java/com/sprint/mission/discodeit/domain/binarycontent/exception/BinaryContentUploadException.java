package com.sprint.mission.discodeit.domain.binarycontent.exception;

import com.sprint.mission.discodeit.common.exception.ErrorCode;

public class BinaryContentUploadException extends BinaryContentException {

    public BinaryContentUploadException(Throwable cause) {
        super(ErrorCode.BINARY_CONTENT_UPLOAD_FAILED, cause);
    }
}
