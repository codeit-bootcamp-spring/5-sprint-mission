package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel extends BaseEntity {
    private final List<Message> messages;
    private final List<User> users;
    private String channelName;
    private String description;

    public Channel(String channelName, String description) {
        super(UUID.randomUUID(), System.currentTimeMillis(), System.currentTimeMillis());
        this.channelName = channelName;
        this.description = description;
        this.users = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public void update(String channelName, String description) {
        boolean isUpdated = false;
        if (channelName != null && !channelName.equals(this.channelName)) {
            this.channelName = channelName;
            isUpdated = true;
        }
        if (description != null && !description.equals(this.description)) {
            this.description = description;
            isUpdated = true;
        }

        if (isUpdated) {
            this.updatedAt = System.currentTimeMillis();
        }
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<User> getUsers() {
        return users;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getDescription() {
        return description;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
        }
    }

    public void removeUser(User user) {
        if (users.contains(user)) {
            users.remove(user);
        }
    }

    public void addMessage(Message message) {
        if (!messages.contains(message)) {
            messages.add(message);
        }
    }

    public void removeMessage(Message message) {
        if (messages.contains(message)) {
            messages.remove(message);
        }
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", channelName='" + channelName + '\'' +
                ", description='" + description + '\'' +
                ", usersCount=" + users.size() +
                ", messagesCount=" + messages.size() +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEntity that = (BaseEntity) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
