package com.sprint.mission.discodeit.entity;

public class User extends Base {

    private String name;
    private String password;

    public User(String name, String password) {
        if(name==null || name.isBlank()){ throw new IllegalArgumentException("이름은 null이 될 수 없음");}
        if(password==null || password.isBlank()){ throw new IllegalArgumentException("비밀번호는 null이 될 수 없음");}
        this.name = name;
        this.password = password;
    }

    public String getName() {return name;}

    public void updateName(String newName) {
        if(name==null || name.isBlank()){ throw new IllegalArgumentException("이름은 null이 될 수 없음");}
        this.name = newName;
        updateTimestamp();
    }
    public String getPassword() {return password;}
    public void updatePassword(String newPassword) {
        if(password==null || password.isBlank()){ throw new IllegalArgumentException("비밀번호는 null이 될 수 없음");}
        this.password = newPassword;
        updateTimestamp();
    }

    @Override
    public String toString() {
        return "\nid: " + getId() + "|  name: " + name;
    }
}
