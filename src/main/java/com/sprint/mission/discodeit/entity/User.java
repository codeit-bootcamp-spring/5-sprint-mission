package com.sprint.mission.discodeit.entity;

import java.util.Objects;

public class User extends BaseEntity {
    private String name;
    private String nickname;
    private String password;
    private Long deletedAt;

    public User(String name, String nickname, String password) {
        this.name = name;
        this.nickname = nickname;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public Long getDeletedAt() {
        return deletedAt;
    }

    public void update(String name, String nickname, String password) {
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        setUpdatedAt(System.currentTimeMillis());
    }

    public void withdraw() {
        this.deletedAt = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) && Objects.equals(nickname, user.nickname) && Objects.equals(password, user.password) && Objects.equals(deletedAt, user.deletedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nickname, password, deletedAt);
    }

    @Override
    public String toString() {
        return super.toString() + " User{" +
                "name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
