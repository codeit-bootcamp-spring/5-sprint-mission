package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private UUID id;                //고유 아이디
    private String password;        //비밀번호
    private String name;            //이름
    private Long createAt;          //생성 시간
    private Long updateAt;          //수정 시간

    public User() {
        this.id = UUID.randomUUID();
        this.createAt = System.currentTimeMillis();
    }

    public User(String password, String name) {
        this.id = UUID.randomUUID();
        this.createAt = System.currentTimeMillis();
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

    public Long getCreateAt() {
        return createAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
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
                ", 생성 시간 = " + createAt +
                ", 수정 시간 = " + updateAt +
                " }";
    }
}
