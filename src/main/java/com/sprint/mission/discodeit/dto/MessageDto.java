package com.sprint.mission.discodeit.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class MessageDto {

    @Getter
    @RequiredArgsConstructor
    public static class CreateRequest {
        UUID channelId;
        UUID authorId;
        String text;
        List<MultipartFile> additionalFiles;
    }

    @Getter
    @RequiredArgsConstructor
    public static class UpdateRequest {
        UUID id;
        String text;
    }

    @Getter
    @Builder
    public static class DetailResponse {
        UUID channelId;
        UUID authorId;
        UUID id;
        String authorName;
        String channelName;
        String text;
        Instant createdAt;
        Instant updatedAt;
    }
}
