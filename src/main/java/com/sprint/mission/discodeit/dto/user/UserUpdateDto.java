package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.lang.Nullable;

import java.util.UUID;

/**
 * 사용자 업데이트 요청 DTO
 * <p>{@link #name} - 중복 불가</p>
 * <p>{@link #email} - 중복 불가</p>
 * <p>{@link #profileImage} - 프로필 이미지(선택사항 null 허용)</p>
 */
public record UserUpdateDto(
        UUID id,
        @Nullable String name,
        @Nullable String email,
        @Nullable BinaryContent profileImage
) {
}
