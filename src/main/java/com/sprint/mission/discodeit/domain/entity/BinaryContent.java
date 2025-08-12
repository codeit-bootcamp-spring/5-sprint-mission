package com.sprint.mission.discodeit.domain.entity;

import lombok.Getter;

import java.util.Objects;

@Getter
public class BinaryContent extends BaseEntity {
    private String filename;
    private String contentType;
    private long size;
    private byte[] bytes;

    public BinaryContent(String filename, String contentType, byte[] bytes) {
        set(filename, contentType, bytes);
    }

    private static String requireNonBlank(String s, String msg) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(msg);
        return s;
    }

    public void set(String filename, String contentType, byte[] data) {
        this.filename = requireNonBlank(filename, "filename must not be blank");
        this.contentType = requireNonBlank(contentType, "contentType must not be blank");
        Objects.requireNonNull(data, "data must not be null");
        this.size = data.length;
        this.bytes = data.clone();
        touch();
    }

    public byte[] getBytes() {
        return bytes == null ? null : bytes.clone();
    }

    @Override
    public String toString() {
        return "BinaryContent[id=%s, filename=%s, contentType=%s, size=%d]"
                .formatted(getId(), filename, contentType, size);
    }
}
