package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class BinaryContentNotFoundException extends BinaryContentException {

	public BinaryContentNotFoundException() {
		super(ErrorCode.BINARY_CONTENT_NOT_FOUND);
	}

	public static BinaryContentNotFoundException withId(String contentId){
		BinaryContentNotFoundException binaryContentException = new BinaryContentNotFoundException();
		binaryContentException.addDetails("contentId", contentId);
		return binaryContentException;
	}
}
