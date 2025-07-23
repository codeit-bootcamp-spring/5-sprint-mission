package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class User {
    private final UUID id;
    private final String discriminator;   // 유저의 디스코드 태그 - #7533

    private final Long createAt;    // DB timestamp
    private Long modifyAt;

    private String email;
    private String password;
    private String username;
    private String status;

    public User(String email, String username, String password, String discriminator, String status) {
        this.id = UUID.randomUUID();
        Instant now = Instant.now();
        this.createAt = now.getEpochSecond();

        this.email = email;
        this.username = username;
        this.password = password;
        this.discriminator = discriminator;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public long getCreateAt() {
        return createAt;
    }

    public long getModifyAt() {
        return modifyAt;
    }

    public String getEmail() { return email; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public String getDiscriminator() { return discriminator; }

    public String getStatus() { return status; }

    public void update(UserDTO userDTO) {
        this.email =  userDTO.getEmail();
        this.username = userDTO.getUsername();
        this.password = userDTO.getPassword();
        this.status = userDTO.getStatus();

        Instant now = Instant.now();
        modifyAt = now.getEpochSecond();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", discriminator='").append(discriminator).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", createAt=").append(createAt);
        sb.append(", modifyAt=").append(modifyAt);
        sb.append('}');
        return sb.toString();
    }
}
