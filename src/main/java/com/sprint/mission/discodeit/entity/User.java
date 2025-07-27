package com.sprint.mission.discodeit.entity;

import java.util.Objects;
import java.util.UUID;

public class User {
    private UUID id; // 자동 랜덤 부여
    private Long createdAt; // DB timestamp
    private Long updatedAt;

    // 내가 디스 코드에서 필요한 필드를 추가적으로 설계하는 자리
    private String username; // 유저의 기본 이름
    private String password;

    // 권한, 이메일, 이후 필요한 정보들 더.. 선언해도 된다.


    public User(String username, String password) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.createdAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    
    public void update(String username, String password) {
        // 업데이트 코드 작성
        this.username = username;
        this.password = password;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "User[" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(createdAt, user.createdAt) && Objects.equals(updatedAt, user.updatedAt) && Objects.equals(username, user.username) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, updatedAt, username, password);
    }
}
