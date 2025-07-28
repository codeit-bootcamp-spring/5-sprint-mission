package com.sprint.mission.discodeit.entity;

import java.util.Objects;

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
        return userName;
    }

    public void updateUserName(String userName) {
        if(!this.userName.equals(userName)){
            this.userName = userName;
            super.updateUpdatedAt();
        }
    }

    public String getEmail() {
        return email;
    }

    public void updateEmail(String email) {
        if(!this.email.equals(email)){
            this.email = email;
            super.updateUpdatedAt();
        }
    }

    public String getPassword() {
        return password;
    }

    public void updatePassword(String password) {
        if(!this.password.equals(password)){
            this.password = password;
            super.updateUpdatedAt();
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void updatePhoneNumber(String phoneNumber) {
        if(!this.phoneNumber.equals(phoneNumber)){
            this.phoneNumber = phoneNumber;
            super.updateUpdatedAt();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userName, user.userName) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(phoneNumber, user.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, email, password, phoneNumber);
    }
}
