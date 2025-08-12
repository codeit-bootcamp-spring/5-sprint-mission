package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.service.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.service.dto.auth.LoginResult;

/**
 * 인증 관련 유스케이스를 제공하는 서비스
 * - 같은 레이어(Service)에는 의존하지 않고 Repository만 의존
 */
public interface AuthService {
    /**
     * 로그인 시도
     * - username, password가 일치하는 사용자가 있으면 사용자 정보를 반환
     * - 없으면 예외 발생
     */
    LoginResult login(LoginRequest request);
}
