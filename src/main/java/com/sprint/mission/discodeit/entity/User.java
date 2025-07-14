package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {
    private String name;
    private Boolean isOnline;

    public User(String name, boolean isOnline) {
        super();
        this.name = name;
        this.isOnline = isOnline;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    private void setOnline(Boolean online) {
        isOnline = online;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public void update(String name, boolean isOnline) {
        setName(name);
        setOnline(isOnline);
        setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", isOnline=" + isOnline +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}
