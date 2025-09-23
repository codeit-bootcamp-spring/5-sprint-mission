package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class FileIOErrorException extends BinaryContentException {
    public FileIOErrorException() {
        super(ErrorCode.FILE_UPLOAD_ERROR);
    }

    public FileIOErrorException(Throwable cause) {
        super(ErrorCode.FILE_UPLOAD_ERROR, cause);
    }

    public static FileIOErrorException withDetails(String fileName, String reason) {
        FileIOErrorException exception = new FileIOErrorException();
        exception.addDetail("fileName", fileName);
        exception.addDetail("reason", reason);
        return exception;
    }

    public static FileIOErrorException withStorage(String fileName, Throwable cause) {
        FileIOErrorException exception = new FileIOErrorException(cause);
        exception.addDetail("fileName", fileName);
        exception.addDetail("errorType", "STORAGE_ERROR");
        return exception;
    }

    public static FileIOErrorException withFormat(String fileName, String contentType, String expectedFormat) {
        FileIOErrorException exception = new FileIOErrorException();
        exception.addDetail("fileName", fileName);
        exception.addDetail("contentType", contentType);
        exception.addDetail("expectedFormat", expectedFormat);
        exception.addDetail("errorType", "INVALID_FORMAT");
        return exception;
    }
}
