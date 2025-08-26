package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;                //고유 아이디
    private String password;        //비밀번호
    private String name;            //이름
    private final Long createdAt;          //생성 시간
    private Long updatedAt;          //수정 시간

    public User() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
    };

    public User(String password, String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.password = password;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
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

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "사용자 { " +
                "아이디 = " + id +
                ", 비밀번호 = '" + password + '\'' +
                ", 이름 = '" + name + '\'' +
                ", 생성 시간 = " + createdAt +
                ", 수정 시간 = " + updatedAt +
                " }";
    }
}
