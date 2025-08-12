package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 바이너리 데이터(이미지/파일)를 표현하는 불변 도메인.
 * - 수정 불가 요구사항: updatedAt 필드 없음
 * - User/Message에서 BinaryContent의 id만 참조(이쪽에서 역참조하지 않음)
 */
@Getter
public final class BinaryContent {

    // 고유 식별자
    private final UUID id;

    // 메타데이터
    private final String contentType;   // 예: image/png, application/pdf
    private final String originalName;  // 원본 파일명
    private final long size;            // 바이트 크기
    private final String storageKey;    // 파일 저장소 키/경로(예: S3 key, 로컬 경로 등)

    // 실제 데이터(저장소 전략에 따라 null 일 수도 있음)
    private final byte[] data;

    // 생성 시각
    private final Instant createdAt;

    // 생성자는 private: 정적 팩토리 사용
    private BinaryContent(UUID id,
                          String contentType,
                          String originalName,
                          long size,
                          String storageKey,
                          byte[] data,
                          Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.contentType = Objects.requireNonNull(contentType, "contentType");
        this.originalName = Objects.requireNonNull(originalName, "originalName");
        if (size < 0) throw new IllegalArgumentException("size must be >= 0");
        this.size = size;
        this.storageKey = Objects.requireNonNull(storageKey, "storageKey");
        this.data = data; // 외부에서 방어적 복사하고 넘기는 것을 권장
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    // 정적 팩토리(메모리 저장 방식)
    public static BinaryContent of(String contentType,
                                   String originalName,
                                   long size,
                                   String storageKey,
                                   byte[] data) {
        return new BinaryContent(
                UUID.randomUUID(),
                contentType,
                originalName,
                size,
                storageKey,
                data,
                Instant.now()
        );
    }

    // 정적 팩토리(외부 저장소만 사용하는 경우 data 생략)
    public static BinaryContent reference(String contentType,
                                          String originalName,
                                          long size,
                                          String storageKey) {
        return new BinaryContent(
                UUID.randomUUID(),
                contentType,
                originalName,
                size,
                storageKey,
                null,
                Instant.now()
        );
    }

}
