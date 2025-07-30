package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    private UUID id;
    private Long createdAt;
    private Long updatedAt;


    private String email;
    private String nick;
    private String pass;


//    private List<UUID> messageId;
//    private List<UUID> channelId;

//    public List<UUID> getMessageId() {
//        return messageId;
//    }
//
//    public void updateMessageId(UUID messageId) {
//        this.messageId.add(messageId);
//    }
//
//    public List<UUID> getChannelId() {
//        return channelId;
//    }
//
//    public void updateChannelId(UUID channelId) {
//        this.channelId.add(channelId);
//    }


    public String getEmail() {
        return email;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public String getNick() {
        return nick;
    }

    public void updateNick(String nick) {
        this.nick = nick;
    }

    public String getPass() {
        return pass;
    }

    public void updatePass(String pass) {
        this.pass = pass;
    }


    public UUID getId() {
        return id;
    }

    public void updateId(UUID id) {
        this.id = id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void updateCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void updateUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }


    public User(){

    }

    public User(String nick,String email, String pass) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().toEpochMilli();
        this.nick = nick;
        this.email = email;
        this.pass = pass;
//        this.messageId = new ArrayList<>();
//        this.channelId = new ArrayList<>();

    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", email='" + email + '\'' +
                ", nick='" + nick + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }
}
