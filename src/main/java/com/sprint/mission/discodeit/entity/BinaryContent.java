package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델입니다. 사용자의 프로필 이미지, 메시지에 첨부된 파일을 저장하기 위해 활용합니다.
 * <p>{@link #id} - User 또는 Message의 PK로 사용</p>
 * <p>{@link #createdAt}</p>
 * <p>{@link #fileName} - 파일명</p>
 * <p>{@link #contentType} - MIME 타입</p>
 * <p>{@link #content}</p>
 * <p>{@link #fileSize}</p>
 **/
@Getter
public class BinaryContent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;

    // 바이너리 정보
    private final String fileName;
    private final String contentType;
    private final byte[] content;
    private final long fileSize;


    public BinaryContent(String fileName, String contentType, byte[] content, long fileSize) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
        this.fileSize = fileSize;
    }

    /**
     * DTO 기반 생성
     */
    public static BinaryContent from(BinaryContentDto dto) {
        return new BinaryContent(
                dto.getFileName(),
                dto.getContentType(),
                dto.getContent(),
                dto.getFileSize()
        );
    }

    /**
     * MultipartFile 기반 생성 (웹 업로드 대응)
     */
    public static BinaryContent from(MultipartFile file) {
        try {
            return new BinaryContent(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes(),
                    file.getSize()
            );
        } catch (IOException e) {
            throw new RuntimeException("파일 변환 실패", e);
        }
    }



    public String getCreatedAtFormatted() {
        LocalDateTime dateTime = LocalDateTime.ofInstant(createdAt, ZoneId.systemDefault());
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}


