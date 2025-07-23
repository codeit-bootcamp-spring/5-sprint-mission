package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class UserDTO {
    private UUID id;
    private String discriminator;
    private String email;
    private String password;
    private String username;
    private String status;

    public UserDTO(UUID id, String email, String username, String password, String discriminator, String status) {
        this.id = id;
        this.discriminator = discriminator;
        this.email = email;
        this.password = password;
        this.username = username;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserDTO{");
        sb.append("id=").append(id);
        sb.append(", discriminator='").append(discriminator).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
