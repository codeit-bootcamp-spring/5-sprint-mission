package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // 1. 필드 선언
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    private String name;
    private String email;
    private String password;

    // 2. 생성자
    public User(String name, String email, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = null;

        this.name = name;
        this.email = email;
        this.password = password;

    }

    // 3. Getter
    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // 4. 업데이트 메서드
    public void updatedName(String name){
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }


}