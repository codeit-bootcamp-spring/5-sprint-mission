package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

/**
 * 사용자 생성 요청 DTO
 * <p>{@link #name} - 중복 불가</p>
 * <p>{@link #email} - 중복 불가</p>
 * <p>{@link #password} - 비밀번호</p>
 * <p>{@link #profileImage} - 프로필 이미지(선택사항 null 허용)</p>
 */
public record CreateUserRequest(
        @NotBlank(message = "이름을 입력해주세요")
        String name,

        @NotBlank(message = "이메일을 입력해주세요")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요")
        String password,

        @Nullable
        MultipartFile profileImage
) {
}
