package com.sprint.mission.discodeit.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//@Data
@Getter
@Setter
@ToString
public class Channel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private ChannelType type;
    private String name;
    private String description;
    private Map<UUID,String> userId;
    public Channel(){}
    public Channel(ChannelType type,UUID userId){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.type = type;
        this.userId.put(userId,"");
    }
    public Channel(ChannelType type, String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.type = type;
        this.name = name;
        this.description = description;
    }
    public void addUserId(UUID userId){
        this.userId.put(userId,"");
    }
    public void deleteUserId(UUID userId){
        this.userId.remove(userId);
    }
    public List<UUID> getUserIds(){
        List<UUID> userIds = new ArrayList<>(this.userId.keySet());
        if(userIds.isEmpty()) {
            return new ArrayList<>();
        }
        return userIds;
    }


    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public ChannelType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
