package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public class AuthDto {

    public record Login(
            String email,
            String password
    ) {}

    public record View(
            UUID id,
            String email,
            String name,
            boolean isOnline,
            UUID profileImage
    ) {
        @Override
        public String toString() {
            return String.format(
                    "\n[ 로그인 유저 정보 ]\n- ID: %s\n- 이름: %s\n- 이메일: %s\n- 온라인 여부: %s\n- 프로필이미지: %s",
                    id, name, email, isOnline ? "온라인" : "오프라인", profileImage
            );
        }
    }
}
