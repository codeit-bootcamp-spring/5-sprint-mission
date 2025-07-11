package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String name;
    private int age;

    // 생성자
    public User(String name, int age) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.name = name;
        this.age = age;
    }

    // 반환 함수들
    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getName() { return name; }
    public int getAge() { return age; }

    // 업데이트
    public void update(String name, int age) {
        this.name = name;
        this.age = age;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
