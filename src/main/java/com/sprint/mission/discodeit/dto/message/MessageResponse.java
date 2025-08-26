package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.Message;
import lombok.Builder;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

public class MessageResponse {

    @Builder
    public record Detail(
            UUID id,
            String createdAt,
            String updatedAt,
            String content,
            UUID channelId,
            UUID authorId,
            @Nullable
            List<UUID> attachmentIds

    ) {
        public static Detail of(Message message) {
            return Detail.builder()
                    .id(message.getId())
                    .createdAt(message.getCreatedAtFormatted())
                    .updatedAt(message.getUpdatedAtFormatted())
                    .content(message.getContent())
                    .channelId(message.getChannelId())
                    .authorId(message.getUserId())
                    .attachmentIds(message.getFiles())
                    .build();
        }
    }

    @Builder
    public record Updated(
            UUID id,
            String content,
            String createdAt,
            String updateAt
    ) {}
}
