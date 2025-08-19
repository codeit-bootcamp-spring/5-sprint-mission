package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class UserRequest {

    public record Create(
            @NotBlank(message = "이름을 입력해주세요")
            String name,

            @NotBlank(message = "이메일을 입력해주세요")
            String email,

            @NotBlank(message = "비밀번호를 입력해주세요")
            String password
    ) {}

    public record update(
            @NotNull(message = "아이디를 입력해주세요")
            UUID id,
            @Nullable String name,
            @Nullable String email,
            @Nullable MultipartFile profileImage
    ) {}

    public record passwordReset(
            @NotNull(message = "아이디를 입력하세요")
            UUID userId,

            @NotBlank(message = "현재 비밀번호를 입력하세요")
            String password,

            @NotBlank(message = "새로운 비밀번호를 입력하세요")
            String newPassword
    ) {}
}
