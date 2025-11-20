package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "binary_contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContent extends BaseEntity {

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false, length = 100)
    private String contentType;

    public BinaryContent(
        String fileName,
        long size,
        String contentType
    ) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName cannot be null");
        }
        if (contentType == null) {
            throw new IllegalArgumentException("contentType cannot be null");
        }
        if (contentType.length() > 100) {
            throw new IllegalArgumentException("contentType length cannot exceed 100 characters");
        }

        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "BinaryContent[id=%s, fileName=%s, size=%d, contentType=%s]"
            .formatted(getId(), fileName, size, contentType);
    }
}
