package com.sprint.mission.discodeit.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record UserUpdateRequest(
        UUID userId,
        String username,
        String email,
        String password
) {}
