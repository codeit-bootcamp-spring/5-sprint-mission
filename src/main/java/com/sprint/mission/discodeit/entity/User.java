package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {
    private String name;
    private String email;
    private String password;
    private Boolean isOnline;

    public User(String name, String email, String password, boolean isOnline) {
        super();
        this.name = name;
        this.email = email;
        this.password = password;
        this.isOnline = isOnline;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public String getEmail() {
        return email;
    }

    public String getpassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public void update(String name, boolean isOnline) {
        this.name = name;
        this.isOnline = isOnline;
        setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "User{" +
            "name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", isOnline=" + isOnline +
            ", id=" + id +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}
