package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
    @NotBlank(message = "사용자 이름을 입력하세요")
    @Size(min = 3,max=30,message = "사용자 이름은 3자 이상 30자 이하 입니다")
    String username,
    @NotBlank(message = "이메일을 입력하세요")
    @Email(message = "유효한 이메일 형식이어야 합니다")
    @Size(max = 100,message = "이메일은 100자 이하여야 합니다")
    String email,
    @NotBlank(message = "비밀번호를 입력하세요")
    @Size(min = 8,max = 20,message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    String password
) {

}
