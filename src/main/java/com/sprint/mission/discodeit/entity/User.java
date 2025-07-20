package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {
    private String name;

    public User(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void updateName(String name) {
        this.name = name;
        updateTimestamp();
    }
}
