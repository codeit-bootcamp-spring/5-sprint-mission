package com.sprint.mission.discodeit.auth.domain.exception;

import com.sprint.mission.discodeit.common.domain.exception.ErrorCode;

public class MissingRefreshTokenCookieException extends AuthException {

    public MissingRefreshTokenCookieException() {
        super(ErrorCode.MISSING_REFRESH_TOKEN);
    }
}
