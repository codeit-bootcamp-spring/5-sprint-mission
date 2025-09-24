package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

//파일 업로드 실패
public class FileUploadFailedException extends BinaryContentException {

  public FileUploadFailedException() {
    super(ErrorCode.FILE_UPLOAD_FAILED);
  }

  public FileUploadFailedException(Map<String, Object> details) {
    super(ErrorCode.FILE_UPLOAD_FAILED, details);
  }
}
