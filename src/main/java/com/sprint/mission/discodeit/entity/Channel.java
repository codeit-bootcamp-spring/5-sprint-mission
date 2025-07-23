package com.sprint.mission.discodeit.entity;

public class Channel extends BaseEntity {
    private User ownerUser;
    private String channelName;
    private ChannelType type;
    private String topic;

    public Channel(
            String name, ChannelType type, User ownerUser,String topic
    ) {
        super();
        this.ownerUser = ownerUser;
        this.channelName = name;
        this.type = type;
        this.topic = topic;
    }

    public String getChannelName() {
        return channelName;
    }

    public void updateName(String name) {
        this.channelName = name;
        super.updateUpdatedAt();
    }

    public ChannelType getType() {
        return type;
    }

    public void updateType(ChannelType type) {
        this.type = type;
        super.updateUpdatedAt();
    }

    public User getOwnerUser() {
        return ownerUser;
    }

    public void updateOwnerUser(User ownerUser) {
        this.ownerUser = ownerUser ;
        super.updateUpdatedAt();
    }

    public String getTopic() {
        return topic;
    }

    public void updateTopic(String topic) {
        this.topic = topic;
        super.updateUpdatedAt();
    }

}
