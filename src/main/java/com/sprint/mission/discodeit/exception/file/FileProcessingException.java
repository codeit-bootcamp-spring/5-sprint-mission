package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class FileProcessingException extends FileException {
	public FileProcessingException() {
		super(ErrorCode.FILE_PROCESSING_FAIL);
	}

	public static FileProcessingException handleAttachmentFailure(String fileName, String contentType){
		FileProcessingException exception = new FileProcessingException();
		exception.addDetails("file Name", fileName);
		exception.addDetails("file context type", contentType);
		return exception;
	}

}
