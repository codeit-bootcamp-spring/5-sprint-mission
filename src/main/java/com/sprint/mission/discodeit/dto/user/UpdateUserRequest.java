package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.BinaryContent;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 사용자 업데이트 요청 DTO
 * <p>{@link #name} - 중복 불가</p>
 * <p>{@link #email} - 중복 불가</p>
 * <p>{@link #profileImage} - 프로필 이미지(선택사항 null 허용)</p>
 */
public record UpdateUserRequest(
        @NotNull(message = "아이디를 입력해주세요")
        UUID id,
        @Nullable String name,
        @Nullable String email,
        @Nullable MultipartFile profileImage
) {
}
