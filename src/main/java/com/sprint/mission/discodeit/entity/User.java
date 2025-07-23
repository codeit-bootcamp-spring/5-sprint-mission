package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {
    private String userName;
    private String email;
    private String password;
    private String phoneNumber;

    public User(
            String userName, String email, String password, String phoneNumber
    ) {
        super();
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return this.userName;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
        super.updateUpdatedAt();
    }

    public String getEmail() {
        return email;
    }

    public void updateEmail(String email) {
        this.email = email;
        super.updateUpdatedAt();
    }

    public String getPassword() {
        return this.password;
    }

    public void updatePassword(String password) {
        this.password = password;
        super.updateUpdatedAt();
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        super.updateUpdatedAt();
    }

}
