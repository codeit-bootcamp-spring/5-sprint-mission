package com.sprint.mission.discodeit.dto.message.request;

import com.sprint.mission.discodeit.entity.BinaryContent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

public record MessageCreateDto(
        @NotNull(message = "아이디를 입력해주세요")
        UUID userId,
        @NotNull(message = "아이디를 입력해주세요")
        UUID channelId,
        @NotBlank(message = "메시지 내용을 입력해주세요")
        String content,
        @Nullable
        List<BinaryContent> files
) {
}
