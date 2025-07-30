package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class Channel extends BaseEntity {
    private String channelName; //채널명
    private String description; //채널 이름

    public Channel(String channelName, String description){
        super();
        this.channelName=channelName;
        this.description=description;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getDescription() {
        return description;
    }


    public void update(String channelName, String description){
        super.update();
        this.description=description;
        this.channelName=channelName;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Channel{");
        sb.append("channelName='").append(channelName).append('\'');
        sb.append(", description='").append(description);
        sb.append('}');
        return sb.toString();
    }
}
