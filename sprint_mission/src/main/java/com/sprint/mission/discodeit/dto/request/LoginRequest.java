package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "이름을 입력하세요")
    @Size(min = 3,max=30,message = "사용자 이름은 3자 이상 30자 이하입니다.")
    String username,
    @NotBlank(message = "비밀번호를 입력하세요")
    @Size(min=8,max=20,message="비밀번호는 8자 이상 20자 이하입니다.")
    String password
) {

}
