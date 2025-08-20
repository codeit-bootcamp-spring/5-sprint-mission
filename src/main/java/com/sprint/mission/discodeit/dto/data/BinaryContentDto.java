// dto/data/BinaryContentDto.java
package com.sprint.mission.discodeit.dto.data;

public record BinaryContentDto(
        Long id,
        String filename,
        Long size,           // bytes
        String contentType,  // e.g. "image/png"
        String checksum,     // 선택
        String createdAt
) {}
