package com.sprint.mission.discodeit.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

public class BinaryContentDto {

    @Getter
    @Builder
    public static class CreateRequest{
        // TODO 추후 요구 조건에 맞춰서 변경 필요할듯?
        MultipartFile file;
    }

    @Getter
    @Builder
    public static class DetailResponse{
        UUID id;
        String name;
        String contentType;
        Long size;
        Instant createdAt;
        byte[] content;
    }
}
