package com.sprint.mission.discodeit.entity;

import java.util.UUID;

//엔티티
public class Message {
    //필드
    private UUID id; // 고유 id (내부 식별자)
    private Long createdAt; // 생성시간
    private Long updatedAt; // 수정시간
    private String content; // 내용
    private UUID sender; // 보낸 사람
    private UUID receiver; // 받는 사람



    //기본생성자
    //매개변수X
    public Message() {
        this.id = UUID.randomUUID(); //생성자 내부 초기화
        this.createdAt = System.currentTimeMillis(); //생성자 내부 초기화
        this.updatedAt = createdAt; //처음 생성시 수정시간을 생성시간으로 맞춰줌
    }


    //일반 생성자
    //사용자로부터 받는 값
    public Message(String content, UUID sender, UUID receiver) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
    }

    //Getter
    public UUID getId() {
        return id;
    }

    public Long getCreateAt() {
        return createdAt;
    }

    public Long getUpdateAt() {
        return updatedAt;
    }

    //메서드
    public void updateTime() {
        this.updatedAt = System.currentTimeMillis();
    }

    public String getContent() {
        return content;
    }

    public UUID getSender() {
        return sender;
    }

    public UUID getReceiver() {
        return receiver;
    }

    //toString
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", content='" + content + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                '}';
    }
}



