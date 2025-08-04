package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.UUID;

@Getter
public class User extends Base  {

    private String name;
    private String email;
    private UUID profileId;
    @JsonIgnore private String password;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void updateName(String newName) {
        this.name = newName;
        updateTimestamp();
    }

    public void updateEmail(String newEmail) {
        this.email = newEmail;
        updateTimestamp();
    }

    public void updateProfileId(UUID newProfileId) {
        this.profileId = newProfileId;
        updateTimestamp();
    }

    public void updatePassword(String currentPassword, String newPassword) {
        if(!this.password.equals(currentPassword)){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }
        if(newPassword==null || newPassword.isBlank()){
            throw new IllegalArgumentException("새 비밀번호는 null이거나 공백일 수 없습니다.");
        }
        if (newPassword.equals(currentPassword)) {
            throw new IllegalArgumentException("새 비밀번호는 기존 비밀번호와 달라야 합니다.");
        }
        // 추후 hash 처리 로직 추가
        this.password = newPassword;
        updateTimestamp();
    }

    @Override
    public String toString() {
        return String.format(
                "\n아이디: %-10s  이름: %-10s  이메일: %-10s",
                getId(), getName(), getEmail()
        );
    }
}
