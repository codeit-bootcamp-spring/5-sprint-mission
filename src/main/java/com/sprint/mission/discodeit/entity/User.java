package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;


public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 1. 필드 선언
    private final UUID id;
    private final Long createdAt;
    private final String email;
    private final transient String password; // 직렬화에서 제외됨

    private Long updatedAt;
    private String name;


    // 2. 생성자
    private User(UUID id, String name,String email, String password, Long createdAt ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = null;  // 생성 시점에는 updatedAt은 null
    }

    public User (String name, String email, String password) {
        this(UUID.randomUUID(), name, email, password, System.currentTimeMillis());
    }

    private User(UUID id, String name, String email,Long createdAt) {
        this(id, name, email, null, createdAt);
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

//    // 5. 민감 데이터 보안 메서드
//    public User toSafeUser() {
//        return new User(this.id, this.name, this.email, this.createdAt); // password 제외
//    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }


}