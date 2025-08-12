package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

/**
 * 사용자 조회 응답 DTO
 * <p>{@link #id} - 아이디</p>
 * <p>{@link #name} - 이름</p>
 * <p>{@link #email} - 이메일</p>
 * <p>{@link #online} - 온라인 여부</p>
 */
public record UserViewDto(
        UUID id,
        String name,
        String email,
        boolean online
) {
}
