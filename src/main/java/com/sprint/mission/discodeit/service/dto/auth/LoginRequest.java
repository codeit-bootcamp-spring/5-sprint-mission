package com.sprint.mission.discodeit.service.dto.auth;

/**
 * 로그인 요청 DTO
 * - username, password를 묶어서 전달
 */
public class LoginRequest {
    public final String username; // 로그인 시도한 사용자명
    public final String password; // 평문/해시 여부는 상위 레이어 정책에 따름

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
