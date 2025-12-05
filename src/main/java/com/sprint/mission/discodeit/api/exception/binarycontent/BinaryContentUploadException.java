package com.sprint.mission.discodeit.api.exception.binarycontent;

import com.sprint.mission.discodeit.api.exception.ErrorCode;

public class BinaryContentUploadException extends BinaryContentException {

    public BinaryContentUploadException(Throwable cause) {
        super(ErrorCode.BINARY_CONTENT_UPLOAD_FAILED, cause);
    }
}
