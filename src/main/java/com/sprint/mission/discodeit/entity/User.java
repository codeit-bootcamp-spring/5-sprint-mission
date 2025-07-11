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
        final StringBuffer sb = new StringBuffer("User{");
        sb.append("id=").append(id);
        sb.append(", createdAt='").append(createdAt).append('\'');
        sb.append(", updatedAt='").append(updatedAt).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.print("hello world");
    }


}
