package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.UUID;

public class LoginDto {

    public record request(
            @NotBlank(message = "이메일을 입력해주세요")
            String email,
            @NotBlank(message = "비밀번호를 입력해주세요")
            String password
    ) {}

    @Builder
    public record response(
            UUID id,
            String email,
            String name,
            boolean isOnline,
            String imageUrl
    ) {}
}
