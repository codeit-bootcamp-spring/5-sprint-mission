package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 바이너리 데이터(이미지/파일)를 표현하는 불변 도메인.
 * - 수정 불가 요구사항: updatedAt 필드 없음
 * - User/Message에서 BinaryContent의 id만 참조(이쪽에서 역참조하지 않음)
 */
@Getter
public final class BinaryContent implements Serializable {

    private static final long serialVersionUID = 1L;
    // 고유 식별자
    private final UUID id;
    // 생성 시각
    private final Instant createdAt;

    private String fileName;
    private Long size;
    private String contentType;
    private byte[] bytes;

    public BinaryContent(String fileName, Long size, String contentType, byte[] bytes){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.fileName=fileName;
        this.size=size;
        this.contentType=contentType;
        this.bytes = bytes;
    }

}
