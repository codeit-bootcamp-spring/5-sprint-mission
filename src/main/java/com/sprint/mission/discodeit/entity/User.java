package com.sprint.mission.discodeit.entity;

import java.io.Serializable;

public class User extends Base implements Serializable {

    private String name;
    private String email;
    private String password;

    public User(String name, String email, String password) {
        if(name==null || name.isBlank()){ throw new IllegalArgumentException("이름은 null이 될 수 없음");}
        if(email==null || email.isBlank()){ throw new IllegalArgumentException("이메일은 null이 될 수 없음");}
        if(password==null || password.isBlank()){ throw new IllegalArgumentException("비밀번호는 null이 될 수 없음");}
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void updateName(String newName) {
        if(newName==null || newName.isBlank()){ throw new IllegalArgumentException("이름은 null이 될 수 없음");}
        this.name = newName;
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

    public String getEmail() {
        return email;
    }

    public void updateEmail(String newEmail) {
        if(newEmail==null || newEmail.isBlank()){ throw new IllegalArgumentException("이메일은 null이 될 수 없음");}
        this.email = newEmail;
        updateTimestamp();
    }

    @Override
    public String toString() {
        return "id: " + getId() + "|  name: " + name + "|  email: " + email;
    }
}
