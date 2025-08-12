package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userName;
    private String email;
    private String password;
    private String phoneNumber;
    private UUID profileId;

    public User(
            String userName, String email, String password, String phoneNumber, UUID profileId
    ) {
        super();
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.profileId = profileId;
    }

    public void updateUserName(String userName) {
        if(!this.userName.equals(userName)){
            this.userName = userName;
            super.updateUpdatedAt();
        }
    }

    public void updateEmail(String email) {
        if(!this.email.equals(email)){
            this.email = email;
            super.updateUpdatedAt();
        }
    }

    public void updatePassword(String password) {
        if(!this.password.equals(password)){
            this.password = password;
            super.updateUpdatedAt();
        }
    }

    public void updatePhoneNumber(String phoneNumber) {
        if(!this.phoneNumber.equals(phoneNumber)){
            this.phoneNumber = phoneNumber;
            super.updateUpdatedAt();
        }
    }

    public void updateProfileId(UUID profileId) {
        if(this.profileId == null || !this.profileId.equals(profileId)){
            this.profileId = profileId;
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("User{");
        sb.append("userName='").append(userName).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", phoneNumber='").append(phoneNumber).append('\'');
        sb.append(", profileId=").append(profileId);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userName, user.userName) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(phoneNumber, user.phoneNumber) && Objects.equals(profileId, user.profileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, email, password, phoneNumber, profileId);
    }
}
