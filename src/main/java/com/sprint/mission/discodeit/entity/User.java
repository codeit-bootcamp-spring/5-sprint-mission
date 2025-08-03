package com.sprint.mission.discodeit.entity;


import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;

    public User(String username, String password){
        super();
        this.username=username;
        this.password=password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    public void update(String username,String password){
        super.updateTimestamp();
        this.username=username;
        this.password=password;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("User{");
        sb.append(super.getId());
        sb.append(" username='").append(username).append('\'');
        sb.append(", password='").append(password);
        sb.append("'}");
        return sb.toString();
    }

}
