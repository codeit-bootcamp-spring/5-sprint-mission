package com.sprint.mission.discodeit.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private String code;        // NOT_FOUND, BAD_REQUEST ...
    private String message;     // 에러 원인 출력 ex) 사용자 id를 찾을수 없습니다.
}
