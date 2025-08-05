package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class Channel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;                //고유 아이디
    private String name;            //채널 명
    private String description;     //채널 설명
    private final Long createdAt;          //생성시간
    private Long updatedAt;          //수정 시간

    public Channel() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
    }

    public Channel(String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.name = name;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "채널 { " +
                "아이디 = " + id +
                ", 이름 = '" + name + '\'' +
                ", 설명 = '" + description + '\'' +
                ", 생성 시간 = " + createdAt +
                ", 수정 시간 = " + updatedAt +
                " }";
    }
}
