package com.sprint.mission.discodeit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BinaryContentResponse {
    private UUID id;
    private Instant createdAt;
    private String fileName;
    private String contentType;
    private Long size;
    private UUID userId;
    private UUID messageId;

}
