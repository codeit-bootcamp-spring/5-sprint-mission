package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

//엔티티
@Getter
public class Channel implements Serializable {
    //직렬화된 객체의 버전을 명시적으로 지정
    @Serial
    private static final long serialVersionUID = 1L;
    //필드
    private final UUID id; // 채널고유 id (내부 식별자)
    private final Instant createdAt; // 생성시간
    private Instant updatedAt; // 수정시간
    private String title; // 채널 이름
    private String description; // 채널 설명
    private ChannelType channelType; // 음성채널 or 일반채널

    //기본 생성자
    //매개변수X
    public Channel() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    //일반 생성자
    public Channel(String title, String description, ChannelType channelType) {
        this.id = UUID.randomUUID(); //생성자 내부 초기화
        this.createdAt = Instant.now(); //생성자 내부 초기화
        this.updatedAt = createdAt; //생성자 내부 초기화
        this.title = title;
        this.description = description;
        this.channelType = channelType;
    }

    //복사본 생성자
    public Channel(Channel other) {
        this.id = other.id;
        this.createdAt = other.createdAt;
        this.updatedAt = other.updatedAt;
        this.title = other.title;
        this.channelType = other.channelType;
    }

    //메서드
    public void updateTime() {
        this.updatedAt = Instant.now();
    }

    //toString
    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", channeltype='" + channelType + '\'' +
                '}';
    }
}
