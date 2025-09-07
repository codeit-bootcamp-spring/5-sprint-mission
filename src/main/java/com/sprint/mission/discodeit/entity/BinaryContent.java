package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor // JPA 프록시 생성을 위한 기본 생성자
@Entity
public class BinaryContent extends BaseEntity {

    private String fileName;

    private Long size;

    private String contentType;

    // 생성자 - 도메인 로직에서 사용할 수 있음
    public BinaryContent(String fileName, Long size, String contentType) {

        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
    }
}
