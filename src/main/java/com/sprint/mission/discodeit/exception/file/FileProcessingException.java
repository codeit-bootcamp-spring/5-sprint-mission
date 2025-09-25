package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class FileProcessingException extends FileException {
	public FileProcessingException() {
		super(ErrorCode.FILE_PROCESSING_FAIL);
	}

	public FileProcessingException(Throwable cause) {
		super(ErrorCode.FILE_PROCESSING_FAIL, cause);
	}

	public static FileProcessingException handleAttachmentFailure(String fileName, String contentType) {
		FileProcessingException exception = new FileProcessingException();
		exception.addDetails("file Name", fileName);
		exception.addDetails("file context type", contentType);
		return exception;
	}

	public static FileProcessingException handleAttachmentFailure(Throwable cause) {
		FileProcessingException exception = new FileProcessingException(cause);
		return exception;
	}

}
