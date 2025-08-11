package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Channel extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String channelName; //채널명
    private String description; //채널 이름

    public Channel(String channelName, String description){
        super();
        this.channelName=channelName;
        this.description=description;
    }

    public void update(String channelName, String description){
        super.updateTimestamp();
        this.description=description;
        this.channelName=channelName;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Channel{");
        sb.append(super.getId());
        sb.append(" channelName='").append(channelName).append('\'');
        sb.append(", description='").append(description);
        sb.append("'}");
        return sb.toString();
    }
}
