package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {

    private final UUID id;

    public User(UUID id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                '}';
    }

    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}
