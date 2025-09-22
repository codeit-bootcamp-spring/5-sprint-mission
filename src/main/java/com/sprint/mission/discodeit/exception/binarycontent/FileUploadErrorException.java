package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class FileUploadErrorException extends BinaryContentException {
    public FileUploadErrorException() {
        super(ErrorCode.FILE_UPLOAD_ERROR);
    }

    public FileUploadErrorException(Throwable cause) {
        super(ErrorCode.FILE_UPLOAD_ERROR, cause);
    }

    public static FileUploadErrorException withDetails(String fileName, String reason) {
        FileUploadErrorException exception = new FileUploadErrorException();
        exception.addDetail("fileName", fileName);
        exception.addDetail("reason", reason);
        return exception;
    }

    public static FileUploadErrorException withStorage(String fileName, Throwable cause) {
        FileUploadErrorException exception = new FileUploadErrorException(cause);
        exception.addDetail("fileName", fileName);
        exception.addDetail("errorType", "STORAGE_ERROR");
        return exception;
    }

    public static FileUploadErrorException withFormat(String fileName, String contentType, String expectedFormat) {
        FileUploadErrorException exception = new FileUploadErrorException();
        exception.addDetail("fileName", fileName);
        exception.addDetail("contentType", contentType);
        exception.addDetail("expectedFormat", expectedFormat);
        exception.addDetail("errorType", "INVALID_FORMAT");
        return exception;
    }
}
