package com.sprint.mission.discodeit.binarycontent.domain;

import com.sprint.mission.discodeit.common.domain.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static org.springframework.util.StringUtils.hasText;

@Entity
@Table(name = "binary_contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContent extends BaseUpdatableEntity {

    private static final int MAX_CONTENT_TYPE_LENGTH = 100;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false, length = MAX_CONTENT_TYPE_LENGTH)
    private String contentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BinaryContentStatus status;

    public BinaryContent(
        String fileName,
        long size,
        String contentType
    ) {
        if (!hasText(fileName)) {
            throw new IllegalArgumentException("fileName must not be blank.");
        }
        if (contentType == null) {
            throw new IllegalArgumentException("contentType must not be null.");
        } else if (contentType.length() > MAX_CONTENT_TYPE_LENGTH) {
            throw new IllegalArgumentException("contentType length cannot exceed " + MAX_CONTENT_TYPE_LENGTH);
        }

        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.status = BinaryContentStatus.PROCESSING;
    }

    public BinaryContent updateStatus(BinaryContentStatus newStatus) {
        if (newStatus != null) {
            this.status = newStatus;
        }
        return this;
    }
}
