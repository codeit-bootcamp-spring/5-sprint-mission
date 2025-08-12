package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.lang.Nullable;

/**
 * 사용자 생성 요청 DTO
 * <p>{@link #name} - 중복 불가</p>
 * <p>{@link #email} - 중복 불가</p>
 * <p>{@link #password} - 비밀번호</p>
 * <p>{@link #profileImage} - 프로필 이미지(선택사항 null 허용)</p>
 */
public record UserRegisterDto(
        String name,
        String email,
        String password,
        @Nullable BinaryContent profileImage
) {
}
