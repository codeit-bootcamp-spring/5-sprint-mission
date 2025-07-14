package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public class User extends Base {

    private String name;
    private String password;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {return name;}
    public void updateName(String newName) {
        this.name = newName;
        updateTimestamp();
    }
    public String getPassword() {return password;}
    public void updatePassword(String newPassword) {
        this.password = newPassword;
        updateTimestamp();
    }

    @Override
    public String toString() {
        return "\nid: " + getId() + "|  name: " + name + "\n";
    }
}
