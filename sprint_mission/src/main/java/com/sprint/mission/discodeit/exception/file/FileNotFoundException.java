package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class FileNotFoundException extends FileException {

    public FileNotFoundException() {
        super(ErrorCode.FILE_NOT_FOUND);
    }

    public static FileNotFoundException withId(UUID fildId) {
        FileNotFoundException exception = new FileNotFoundException();
        exception.addDetail("fildId ", fildId);
        return exception;
    }
}
