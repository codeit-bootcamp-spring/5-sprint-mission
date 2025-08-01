package com.sprint.mission.discodeit.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

public class UserDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateRequest {
        private String name;
        private String email;
        private String password;
        private MultipartFile profileImage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
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
