package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final String discriminator;   // 유저의 디스코드 태그 - #7533

    private final Long createAt;    // DB timestamp
    private Long modifyAt;

    private String email;
    private String password;
    private String username;
    private UserStatus status;

    public User(String email, String username, String password, String discriminator, UserStatus status) {
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

    public long getModifyAt() { return modifyAt; }

    public String getEmail() { return email; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public String getDiscriminator() { return discriminator; }

    public UserStatus getStatus() { return status; }

    public void update(String email, String username, String password, UserStatus status) {
        int sameValueCount = 0;
        if(this.email.equals(email)){
            System.out.println("[Alarm] : The original email and the email to be changed are the same.");
            sameValueCount++;
        }
        if(this.username.equals(username)){
            System.out.println("[Alarm] : The original user's name and the user's name to be changed are the same.");
            sameValueCount++;
        }
        if(this.password.equals(password)){
            System.out.println("[Alarm] : The original password and the password to be changed are the same.");
            sameValueCount++;
        }
        if(status.equals(this.status)){
            System.out.println("[Alarm] : The original user status and the user status to be changed are the same.");
            sameValueCount++;
        }
        this.email =  email;
        this.username = username;
        this.password = password;
        this.status = status;

        if (sameValueCount != 4) {
            Instant now = Instant.now();
            modifyAt = now.getEpochSecond();
        }
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
