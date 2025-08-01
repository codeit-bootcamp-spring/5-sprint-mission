package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델입니다. 사용자의 프로필 이미지, 메시지에 첨부된 파일을 저장하기 위해 활용합니다.
 * <p>{@link #id} - User 또는 Message의 PK로 사용</p>
 * <p>{@link #createdAt}</p>
 * <p>{@link #name}</p>
 * <p>{@link #contentType}</p>
 * <p>{@link #data}</p>
 **/
@Getter
public class BinaryContent {

    private final UUID id;
    private final Instant createdAt;

    // 바이너리 정보
    /**
     * 파일명 ex: image.png
     **/
    private final String name;

    /**
     * MIME 타입 ex: "image/png", "application/pdf"
     **/
    private final String contentType;

    /**
     * 바이너리 데이터
     **/
    private final byte[] data;


    public BinaryContent(BinaryContentDto dto) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.name = dto.getName();
        this.contentType = dto.getContentType();
        this.data = dto.getData();
    }

    public String getCreatedAtFormatted() {
        LocalDateTime dateTime = LocalDateTime.ofInstant(createdAt, ZoneId.systemDefault());
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}


