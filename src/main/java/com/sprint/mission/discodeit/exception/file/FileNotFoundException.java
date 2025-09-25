package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class FileNotFoundException extends FileException {
	public FileNotFoundException() {
		super(ErrorCode.FILE_NOT_FOUND);
	}

	public static FileNotFoundException withBinaryId(String BinaryId){
		FileNotFoundException exception = new FileNotFoundException();
		exception.addDetails("BinaryContentId", BinaryId);
		return exception;
	}
}
