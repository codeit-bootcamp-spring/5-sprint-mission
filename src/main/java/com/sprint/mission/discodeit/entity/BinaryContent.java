package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "binary_contents")
public class BinaryContent extends BaseEntity {

    @Column(
        nullable = false
    )
    private String fileName;

    @Column(nullable = false)
    private long size;

    @Column(
        length = 100,
        nullable = false
    )
    private String contentType;

    @Lob
    @Column(nullable = false)
    private byte[] bytes;

    @Override
    public String toString() {
        return "BinaryContent[id=%s, fileName=%s, size=%d, contentType=%s]"
            .formatted(getId(), fileName, size, contentType);
    }
}
