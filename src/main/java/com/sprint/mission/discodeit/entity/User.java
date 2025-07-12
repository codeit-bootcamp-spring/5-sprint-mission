package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User extends BaseEntity {
    private String nickname;

    public User(String nickname) {
        super();
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
        touch();
    }
}
