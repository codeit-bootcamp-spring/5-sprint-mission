package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public class MessageRequest {

    public record create(
            @NotNull(message = "아이디를 입력해주세요")
            UUID userId,
            @NotNull(message = "아이디를 입력해주세요")
            UUID channelId,
            @NotBlank(message = "메시지 내용을 입력해주세요")
            String content,
            @Nullable
            List<MultipartFile> files
    ) {}

    public record update(
            @NotNull(message = "아이디를 입력해주세요")
            UUID id,
            String content
    ) {}
}
