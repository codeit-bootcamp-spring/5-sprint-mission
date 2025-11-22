package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentUploadException extends BinaryContentException {

    public BinaryContentUploadException(Throwable cause) {
        super(ErrorCode.BINARY_CONTENT_UPLOAD_FAILED, cause);
    }
}
