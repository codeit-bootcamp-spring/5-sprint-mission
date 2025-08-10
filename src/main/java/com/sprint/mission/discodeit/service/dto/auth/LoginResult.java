package com.sprint.mission.discodeit.service.dto.auth;

import java.util.UUID;

/**
 * 로그인 결과 DTO
 * - 비밀번호는 절대 노출하지 않음
 */
public class LoginResult {
    public final UUID id;
    public final String username;
    public final String email;

    public LoginResult(UUID id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}
