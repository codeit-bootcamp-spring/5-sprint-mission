package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.dto.binarycontent.FileResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

public class MessageDto {

    public record create(
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

    public record update(
            @NotNull(message = "아이디를 입력해주세요")
            UUID id,
            String content
    ) {
    }

    @Builder
    public record response(
            UUID id,
            UUID userId,
            String userName,
            UUID channelId,
            String channelName,
            String content,
            @Nullable
            List<FileResponseDto> files // 파일 미리보기, 다운로드 가능
    ) {}

}
