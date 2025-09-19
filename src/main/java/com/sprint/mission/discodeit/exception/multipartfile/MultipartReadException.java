package com.sprint.mission.discodeit.exception.multipartfile;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class MultipartReadException extends UploadException {

  public MultipartReadException(Throwable cause) {
    super(ErrorCode.MULTIPART_READ_FAILED, cause);
  }

  public static MultipartReadException withDetail(String key, Object value, Throwable cause) {
    MultipartReadException exception = new MultipartReadException(cause);
    exception.addDetail(key, value);
    return exception;
  }

}
