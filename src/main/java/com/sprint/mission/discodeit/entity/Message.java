package com.sprint.mission.discodeit.entity;

public class Message extends BaseEntity{
    private final Channel channel;
    private final User authorUser;
    private String content;

    public Message(
            String content, Channel channel, User authorUser
    ) {
        this.channel = channel;
        this.authorUser = authorUser;
        this.content = content;
    }

    public Channel getChannel() {
        return channel;
    }

    public User getAuthorUser() {
        return authorUser;
    }

    public String getContent() {
        return content;
    }

    public void updateContent(String content) {
        this.content = content;
        super.updateUpdatedAt();
    }
}
