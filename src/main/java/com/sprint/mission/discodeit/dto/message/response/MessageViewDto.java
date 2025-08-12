package com.sprint.mission.discodeit.dto.message.response;

import com.sprint.mission.discodeit.dto.binarycontent.FileResponseDto;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

public record MessageViewDto(
        UUID id,
        UUID userId,
        String userName,
        UUID channelId,
        String channelName,
        String content,
        @Nullable
        List<FileResponseDto> files // 파일 미리보기, 다운로드 가능
) {
        @Override
        public @NonNull String toString() {
                return """
                📨 {
                    id: %s
                    발신자: %s (%s)
                    채널: %s (%s)
                    내용: %s
                    첨부파일: %s
                }
                """.formatted(
                        id,
                        userName, userId,
                        channelName, channelId,
                        content,
                        files
                );
        }
}
