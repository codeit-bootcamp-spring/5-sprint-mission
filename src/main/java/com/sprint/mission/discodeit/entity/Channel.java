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
    if (channelName != null) {
      this.channelName = channelName;
    }
    if (description != null) {
      this.description = description;
    }

    this.updatedAt = System.currentTimeMillis();
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
    if(!users.contains(user)) {
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
    if(messages.contains(message)) {
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

}
