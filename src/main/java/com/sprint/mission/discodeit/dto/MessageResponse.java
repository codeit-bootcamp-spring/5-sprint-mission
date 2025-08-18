package com.sprint.mission.discodeit.dto;

import lombok.Builder;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

public class MessageResponse {

    @Builder
    public record detail(
            UUID id,
            UUID userId,
            String userName,
            UUID channelId,
            String channelName,
            String content,
            @Nullable
            List<String> fileUrls, // 파일 미리보기, 다운로드 가능
            String createdAt
    ) {}

    @Builder
    public record updated(
            UUID id,
            String content,
            String createdAt,
            String updateAt
    ) {}
}
