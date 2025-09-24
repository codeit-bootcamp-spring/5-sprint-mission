package com.codeit.mission.discodeit.exception.binarycontent;

import com.codeit.mission.discodeit.exception.ErrorCode;

public class FileUploadException extends BinaryContentException {

    public FileUploadException(String fileName, Throwable cause) {
        super(ErrorCode.FILE_UPLOAD_FAILED, "Failed to upload file: " + fileName, cause);
        addDetail("fileName", fileName);
    }
}
