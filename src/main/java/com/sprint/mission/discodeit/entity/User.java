package com.sprint.mission.discodeit.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class User extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(nullable = true)
    private UUID profileId;
    private String username;
    private String password;

    public User(String username, String password){
        super();
        this.username=username;
        this.password=password;
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
