package com.sprint.mission.discodeit.dto.user;

import lombok.Getter;

@Getter
public class UserCreateRequest {
    private final String username;
    private final String email;
    private final String password;

    // 선택: 프로필 이미지 바이트
    private final byte[] profileImage;

    // 기존 사용 코드와 호환되는 3개 인자 생성자 (프로필 이미지 없음)
    public UserCreateRequest(String username, String email, String password) {
        this(username, email, password, null);
    }

    // 프로필 이미지를 같이 넘기고 싶을 때 쓰는 생성자
    public UserCreateRequest(String username, String email, String password, byte[] profileImage) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }

    // BasicUserService.create()에서 쓰는 헬퍼들
    public boolean hasProfileImage() {
        return profileImage != null && profileImage.length > 0;
    }

    // 메서드 명도 서비스 코드에 맞춰서 유지
    public byte[] getNewProfileImage() {
        return profileImage;
    }
    
}
