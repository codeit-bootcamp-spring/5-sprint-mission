package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class BinaryContent extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String filename;
    private String contentType;
    private long size;
    private byte[] data;

    // Simple constructor for testing serialization
    public BinaryContent() {
        super();
    }

    public BinaryContent(UUID id, Instant createdAt, Instant updatedAt, String filename, String contentType, long size, byte[] data) {
        super(id, createdAt, updatedAt);
        this.filename = filename;
        this.contentType = contentType;
        this.size = size;
        this.data = data;
    }
}
