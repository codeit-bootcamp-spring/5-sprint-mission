package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
public class User extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private String nickname;
    private String password;

    public User(String name, String nickname, String password) {
        this.name = name;
        this.nickname = nickname;
        this.password = password;
    }

    public void update(String name, String nickname, String password) {
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return super.toString() + " User{" +
                "name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
