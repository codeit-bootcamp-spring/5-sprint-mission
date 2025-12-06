package com.sprint.mission.discodeit.domain.binarycontent.exception;

import com.sprint.mission.discodeit.global.error.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class BinaryContentNotFoundException extends BinaryContentException {

    public BinaryContentNotFoundException(UUID binaryContentId) {
        super(ErrorCode.BINARY_CONTENT_NOT_FOUND, Map.of("binaryContentId", binaryContentId));
    }
}
