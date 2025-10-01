package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserUpdatePasswordRequest {
    @NotBlank(message = "현재 비밀번호 필수")
    private String currentPassword;
    @NotBlank(message = "바뀔 비밀번호 필수")
    private String newPassword;
}
