package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.Objects;

public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Channel channel;
    private final User authorUser;
    private String content;

    public Message(
            String content, Channel channel, User authorUser
    ) {
        super();
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
        if(!this.content.equals(content)){
            this.content = content;
            super.updateUpdatedAt();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(channel, message.channel) && Objects.equals(authorUser, message.authorUser) && Objects.equals(content, message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel, authorUser, content);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Message{");
        sb.append("channel=").append(channel);
        sb.append(", authorUser=").append(authorUser);
        sb.append(", content='").append(content).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
