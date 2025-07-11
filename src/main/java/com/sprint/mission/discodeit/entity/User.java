package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {


    UUID id;
    String createdAt;
    String updatedAt;


    User(UUID id) {
        this.id = id;

    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                '}';
    }

    public static void main(String[] args) {
        System.out.print("hello world");
    }


}
