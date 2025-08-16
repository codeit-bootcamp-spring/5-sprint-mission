package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BinaryContentResponse {
    private UUID id;
    private String filename;
    private String contentType;
    private long size;
    private byte[] data;
    private Instant createdAt;
}
