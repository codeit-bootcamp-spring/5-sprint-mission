package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private UUID id;                //고유 아이디
    private String name;            //채널 명
    private String description;     //채널 설명
    private Long createAt;          //생성시간
    private Long updateAt;          //수정 시간

    public Channel() {
        this.id = UUID.randomUUID();
        this.createAt = System.currentTimeMillis();
    }

    public Channel(String name, String description) {
        this.id = UUID.randomUUID();
        this.createAt = System.currentTimeMillis();
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

    public Long getCreateAt() {
        return createAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
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
                ", 생성 시간 = " + createAt +
                ", 수정 시간 = " + updateAt +
                " }";
    }
}
