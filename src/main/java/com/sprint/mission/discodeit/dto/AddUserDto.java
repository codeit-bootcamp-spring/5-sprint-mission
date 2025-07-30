package com.sprint.mission.discodeit.dto;

public class AddUserDto {
    private final String userName;
    private final String email;
    private final String password;
    private final String phoneNumber;


    public AddUserDto(String userName, String email, String password, String phoneNumber) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
