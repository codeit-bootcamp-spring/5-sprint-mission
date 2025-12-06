package com.sprint.mission.discodeit.domain.user.domain.exception;

import com.sprint.mission.discodeit.domain.common.exception.ErrorCode;

public class UserProfileUploadException extends UserException {

    public UserProfileUploadException(Throwable cause) {
        super(ErrorCode.USER_PROFILE_UPLOAD_FAILED, cause);
    }
}
