package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "binary_contents")
public class BinaryContent extends BaseEntity {
    @Column(name="file_name", nullable = false, length = 255)
    private String fileName;
    @Column(name="size", nullable = false)
    private Long size;
    @Column(name="content_type", length = 100, nullable = false)
    private String contentType;
    @Column(name="bytes", nullable = false)
    private byte[] bytes;
}
