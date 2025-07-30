package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Channel extends BaseEntity implements Serializable{
    private static final long serialVersionUID = 1L;

    private final User ownerUser;
    private String channelName;
    private final Set<User> users = new HashSet<>();
    private final Set<Message> messages = new HashSet<>();

    public Channel(
            String channelName, User ownerUser
    ) {
        super();
        this.channelName = channelName;
        this.ownerUser = ownerUser;
    }

    public User getOwnerUser() {
        return ownerUser;
    }

    public String getChannelName() {
        return channelName;
    }

    public void updateChannelName(String channelName) {
        if(!this.channelName.equals(channelName)){
            this.channelName = channelName;
            super.updateUpdatedAt();
        }

    }

    public Set<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public void removeUser(User user) {
        this.users.remove(user);
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(ownerUser, channel.ownerUser) && Objects.equals(channelName, channel.channelName) && Objects.equals(users, channel.users) && Objects.equals(messages, channel.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerUser, channelName, users, messages);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Channel{");
        sb.append("ownerUser=").append(ownerUser);
        sb.append(", channelName='").append(channelName).append('\'');
        sb.append(", users=").append(users);
        sb.append(", messages=").append(messages);
        sb.append('}');
        return sb.toString();
    }
}
