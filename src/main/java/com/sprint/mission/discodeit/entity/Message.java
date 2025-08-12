package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

//엔티티
@Getter
public class Message implements Serializable {

    //직렬화된 객체의 버전을 명시적으로 지정
    @Serial
    private static final long serialVersionUID = 1L;
    //필드
    private final UUID id; // 메시지 고유 id (내부 식별자)
    private final Instant createdAt;
    private Instant updatedAt;
    private String content;
    private UUID channelId;
    private UUID sender; // 채널 기준으로 누가 보냈는지
    private List<UUID> attachmentIds; // Message가 가진 첨부파일 ID 리스트


    //기본생성자
    //매개변수X
    public Message() {
        this.id = UUID.randomUUID(); //생성자 내부 초기화
        this.createdAt = Instant.now(); //생성자 내부 초기화
        this.updatedAt = createdAt; //처음 생성시 수정시간을 생성시간으로 맞춰줌
    }


    //일반 생성자
    //사용자로부터 받는 값
    public Message(String content, UUID sender, UUID channelId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
        this.content = content;
        this.channelId = channelId;
        this.sender = sender;
    }

    //복사생성자
    //메세지 객체 안의 있는 값들 복사해서 새로운 Message 만듦
    public Message(Message other) {
        this.id = other.id;
        this.createdAt = other.createdAt;
        this.updatedAt = other.updatedAt;
        this.content = other.content;
        this.channelId = other.channelId;
        this.sender = other.sender;
    }

    //메서드
    public void updateTime() {
        this.updatedAt = Instant.now();
    }

    //toString
    @Override
    public String toString() {
        return "Message{" + "id=" + id + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", content='" + content + '\'' + ", channelId=" + channelId + ", sender=" + sender + '}';
    }
}



