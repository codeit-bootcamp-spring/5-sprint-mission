package com.sprint.mission.discodeit.dto.message;

import lombok.Data;

import java.util.UUID;

@Data
public class MessageUpdateRequest {
    private UUID id; // 수정할 메세지 ID
    private String content; // 수정할 메세지 내용
}
