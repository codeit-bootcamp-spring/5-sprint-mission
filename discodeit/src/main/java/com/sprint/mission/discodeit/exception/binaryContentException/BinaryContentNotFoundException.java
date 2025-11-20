package com.sprint.mission.discodeit.exception.binaryContentException;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentNotFoundException extends BinaryContentException{
    public BinaryContentNotFoundException() {super(ErrorCode.BINARYCONTENT_NOT_FOUND);};

    public static BinaryContentNotFoundException withId(String binaryContentId) {
        BinaryContentNotFoundException binaryContentNotFoundException = new BinaryContentNotFoundException();
        binaryContentNotFoundException.addDetail("binaryContentId", binaryContentId);
        return binaryContentNotFoundException;
    }
}
