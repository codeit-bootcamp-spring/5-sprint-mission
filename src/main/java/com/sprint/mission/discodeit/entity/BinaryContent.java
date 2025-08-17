package com.sprint.mission.discodeit.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;

    private String filename;
    private String contentType;
    private Long size;
    private byte[] bytes;


    public BinaryContent(String fileName, Long size, String contentType, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.filename = fileName;
        this.contentType = contentType;
        this.size = size;
        this.createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
