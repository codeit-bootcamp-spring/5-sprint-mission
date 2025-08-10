package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private String content; // 내용
    private final UUID channelId; // 체널
    private final UUID authorId; // 작성자
//    private List<UUID> attachmentIds; // 첨부 파일

    public Message(String content, UUID channelId, UUID authorId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
//        this.attachmentIds = attachmentIds;
    }

    public void update(String content) {
        this.updatedAt = Instant.now();
        this.content = content;
    }
}
