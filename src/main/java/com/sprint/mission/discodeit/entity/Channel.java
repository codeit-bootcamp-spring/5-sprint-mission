package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Channel extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private ChannelType type;
    private String name; //채널명
    private String description; //채널 이름

    public Channel(ChannelType type, String name, String description){
        super();
        this.type=type;
        this.name=name;
        this.description=description;
    }

    public void update(String newName, String newDescription){
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
            super.updateTimestamp();
        }
    }
}
