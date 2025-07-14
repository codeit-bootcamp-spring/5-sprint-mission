package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private UUID id;
    private String email;
    private String userName;
    private String nickname;
    private String password;
    private String phoneNumber;
    private Long createAt;
    private Long updateAt;

    public User(UUID id, Long createAt, String email, String userName, String nickname, String password, String phoneNumber) {
        this.id = id;
        this.createAt = createAt;
        this.email = email;
        this.userName = userName;
        this.nickname = nickname;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public String getNickname() {
        return nickname;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("----- ").append(userName).append(" info -----\n")
                .append("고유ID: ").append(id).append('\n')
                .append("이메일: ").append(email).append('\n')
                .append("별명: ").append(nickname).append('\n')
                .append("비밀번호: ").append(password).append('\n')
                .append("전화번호: ").append(phoneNumber).append('\n')
                .append("가입시기: ").append(createAt).append('\n')
                .append("프로필 변경 시기:  ").append(updateAt).append('\n');

        return sb.toString();
    }
}
