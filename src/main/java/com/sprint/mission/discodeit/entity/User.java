package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private String name; // 이름
    private String email; // 이메일
    private String password; // 비밀번호
    private UUID profileId; // 프로필 (Null 가능?)

    public User(String name, String email, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void update(String name, String email, String password, UUID profileId) {
        this.updatedAt = Instant.now();
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }
}
