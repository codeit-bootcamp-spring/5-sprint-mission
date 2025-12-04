package com.sprint.mission.discodeit.common.exception.user;

import com.sprint.mission.discodeit.common.exception.ErrorCode;

public class UserProfileUploadException extends UserException {

    public UserProfileUploadException(Throwable cause) {
        super(ErrorCode.USER_PROFILE_UPLOAD_FAILED, cause);
    }
}
