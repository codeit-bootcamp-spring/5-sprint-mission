package com.sprint.mission.discodeit.user.domain.exception;

import com.sprint.mission.discodeit.common.domain.exception.ErrorCode;

public class UserProfileUploadException extends UserException {

    public UserProfileUploadException(Throwable cause) {
        super(ErrorCode.USER_PROFILE_UPLOAD_FAILED, cause);
    }
}
