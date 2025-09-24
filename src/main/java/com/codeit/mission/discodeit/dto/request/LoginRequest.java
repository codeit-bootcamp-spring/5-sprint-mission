package com.codeit.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "사용자명은 필수입니다.")
        @Size(min = 2, max = 50, message = "사용자명은 2자 이상 50자 이하여야 합니다.")
        String username,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 1, max = 100, message = "비밀번호는 1자 이상 100자 이하여야 합니다.")
        String password
) {

}
