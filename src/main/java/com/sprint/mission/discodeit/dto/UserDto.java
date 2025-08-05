package com.sprint.mission.discodeit.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

public class UserDto {

    @Getter
    @Builder
    public static class CreateRequest {
        private String name;
        private String email;
        private String password;
        private MultipartFile profileImage;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class UpdateRequest {
        private UUID id;
        private String name;
        private String email;
        private String password;
        private MultipartFile profileImage;
    }

    @Getter
    @Builder
    public static class SummaryResponse {
        private UUID id;
        private String name;
        private String email;
    }

    @Getter
    @Builder
    @ToString
    public static class DetailResponse {
        private UUID id;
        private String name;
        private String email;
        private UUID profileId;
        private boolean isOnline;
        private Instant createdAt;
        private Instant updatedAt;
    }
}
