package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

//파일 다운로드 실패
public class FileDownloadFailedException extends BinaryContentException {


  public FileDownloadFailedException() {
    super(ErrorCode.FILE_DOWNLOAD_FAILED);
  }

  public FileDownloadFailedException(Map<String, Object> details) {
    super(ErrorCode.FILE_DOWNLOAD_FAILED, details);
  }
}
