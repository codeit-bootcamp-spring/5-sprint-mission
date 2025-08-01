package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.StringJoiner;
import java.util.UUID;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt;
    private Long updatedAt;


    private String username;
    private String email;
    private String password;

    public User(String username) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = Instant.now().getEpochSecond();
        this.updatedAt = createdAt;
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


    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void update(String username, String email, String password) {
      boolean flag = false;
      if(username != null && !username.equals(this.username)) {
          this.username = username;
          flag = true;
      }
      if(email != null && !email.equals(this.username)) {
          this.email = email;
          flag = true;
      }
      if(password != null && !password.equals(this.password)) {
          this.password = password;
          flag = true;
      }
      if(flag) {
          this.updatedAt = Instant.now().toEpochMilli();
      }

    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
