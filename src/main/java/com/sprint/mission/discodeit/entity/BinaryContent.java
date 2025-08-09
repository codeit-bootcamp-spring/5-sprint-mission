package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

// 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델입니다.
// 사용자의 프로필 이미지, 메시지에 첨부된 파일을 저장하기 위해 활용합니다.
// User, Message 도메인 모델과의 의존 관계 방향성을 잘 고려하여 id 참조 필드를 추가하세요.

@Getter
@ToString
public class BinaryContent implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;

    private String fileName;
    private String contentType; // jpg, ... 확장자
    private Long size;
    private byte[] bytes;

    // User, Message 도메인 의존성 추가
    private List<UUID> attachmentIds; // 해당 파일이 연결된 메세지 아이디
    private UUID userId; // 파일 업로드한 유저 아이디(User)

    public BinaryContent(String fileName, String contentType, Long size, byte[] bytes,
                         List<UUID> attachmentIds, UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.bytes = bytes;
        this.attachmentIds = attachmentIds;
        this.userId = userId;
    }
}
