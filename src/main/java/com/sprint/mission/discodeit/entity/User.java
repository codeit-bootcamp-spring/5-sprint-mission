package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private UUID id;
    private Long createAt;
    private Long updateAt;

    //내가 디스코드에서 필요한 필드를 추가적으로 설계하는 자리
    private String username;
    private String password;
    private String email;

    public void setCreateAt(Long createAt) {
        this.createAt = createAt;
    }

    public void setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User(String username, String password) {
        id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        createAt = System.currentTimeMillis();
    }

    public User(String username, String password, String email) {
        id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        createAt = System.currentTimeMillis();
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("User{");
        sb.append("id=").append(id);
        sb.append(", createAt=").append(createAt);
        sb.append(", updateAt=").append(updateAt);
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public Long getCreateAt() {
        return createAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
//권한, 이메일, 이후 필요한 정보를 더 선언해도 된다

    public User(UUID id) {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
