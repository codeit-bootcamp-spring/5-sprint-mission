package com.sprint.mission.discodeit.entity;

import java.util.UUID;

//엔티티
public class Channel {
    //필드
    private UUID id; // 고유 id (내부 식별자)
    private Long createdAt; // 생성시간
    private Long updatedAt; // 수정시간
    private String title; // 채널 이름
    private String description; // 채널 설명
    private String type; // 음성채널 or 일반채널


    //기본 생성자
    //매개변수X
    public Channel() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
    }

    //일반 생성자
    public Channel(String title, String description, String type) {
        this.id = UUID.randomUUID(); //생성자 내부 초기화
        this.createdAt = System.currentTimeMillis(); //생성자 내부 초기화
        this.updatedAt = createdAt; //생성자 내부 초기화
        this.title = title;
        this.description = description;
        this.type = type;
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

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    //메서드
   public void updateTime() {
        this.updatedAt = System.currentTimeMillis();
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
                ", type='" + type + '\'' +
                '}';
    }
}
