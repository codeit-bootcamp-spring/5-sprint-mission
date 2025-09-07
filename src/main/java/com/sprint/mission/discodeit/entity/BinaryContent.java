package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 파일 바이너리 저장소
 * - Message의 첨부파일(N:N, 조인테이블)
 * - User/Channel의 프로필 이미지(1:1, 소유측이 FK 가짐)
 */
@Getter
@Entity
@Table(name = "binary_contents")
public class BinaryContent extends BaseEntity {

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String contentType;

//    @Lob
//    @Column(nullable = false)
//    private byte[] bytes;

    protected BinaryContent() {} // 이걸 왜 만들었지

    public BinaryContent(String fileName, Long size, String contentType) {
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
//        this.bytes = bytes;
    }

}
