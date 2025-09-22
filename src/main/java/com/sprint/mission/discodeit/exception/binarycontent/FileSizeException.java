package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class FileSizeException extends BinaryContentException {
    public FileSizeException() {
        super(ErrorCode.FILE_SIZE_EXCEEDED);
    }

    public static FileSizeException withSizes(String fileName, long actualSize, long maxSize) {
        FileSizeException exception = new FileSizeException();
        exception.addDetail("fileName", fileName);
        exception.addDetail("actualSize", actualSize);
        exception.addDetail("maxSize", maxSize);
        exception.addDetail("actualSizeMB", actualSize / (1024.0 * 1024));
        exception.addDetail("maxSizeMB", maxSize / (1024.0 * 1024));
        return exception;
    }
}
