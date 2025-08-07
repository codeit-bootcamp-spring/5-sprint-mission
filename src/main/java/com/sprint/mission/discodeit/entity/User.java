package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class User extends BaseEntity {
    private String email;
    private String userName;
    private String nickname;
    private String password;
    private String phoneNumber;

    public User(String email, String userName, String nickname, String password, String phoneNumber) {
        this(UUID.randomUUID(), Instant.now().getEpochSecond(), email, userName, nickname, password, phoneNumber);
    }

    public User(UUID id, Long createAt, String email, String userName, String nickname, String password, String phoneNumber) {
        super(id, createAt);
        this.email = email;
        this.userName = userName;
        this.nickname = nickname;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public void updateUser(String email, String userName, String nickname, String password, String phoneNumber) {
        this.email = email;
        this.userName = userName;
        this.nickname = nickname;
        this.password = password;
        this.phoneNumber = phoneNumber;
        super.updateTimeStamp();
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
