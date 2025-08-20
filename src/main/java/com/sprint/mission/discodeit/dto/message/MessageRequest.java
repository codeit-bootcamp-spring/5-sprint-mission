package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public class MessageRequest {

    public record Create(
            @NotNull(message = "사용자아이디를 입력해주세요")
            UUID authorId,
            @NotNull(message = "채널아이디를 입력해주세요")
            UUID channelId,
            @NotBlank(message = "메시지 내용을 입력해주세요")
            String content,
            List<MultipartFile> attachments
    ) {}

    public record update(
            @NotNull(message = "아이디를 입력해주세요")
            UUID id,
            String content
    ) {}
}
