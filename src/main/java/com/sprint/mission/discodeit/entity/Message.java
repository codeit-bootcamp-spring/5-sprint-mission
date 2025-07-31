package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final long createdAt;
    private long updatedAt;

    private final UUID userId;
    private final UUID chId;
    private final String messageKey; //메시지 식별하려고(메시지는 여러개 생성 가능하니까)
    private String message;


    //외부에서 받아오는 생성자들을 선언해줘야 함.
    public Message(UUID userId, UUID chId, String messageKey, String message){
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.userId = userId;
        this.chId = chId;
        this.messageKey = messageKey;
        this.message = message;
    }

    public UUID getId(){
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChId() {
        return chId;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public long getCreatedAt(){
        return createdAt;
    }

    public long getUpdatedAt(){
        return updatedAt;
    }

    public String getMessage(){
        return message;
    }

    public void updateMessage(String message){
        this.message = message;
        this.updatedAt = System.currentTimeMillis();
    }
}
