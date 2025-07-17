package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.userentity.Status;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class User {
  private final UUID id;
  private final long createdAt;
  private final Set<UUID> friends;
  private final Set<UUID> servers;
  private final Set<UUID> chatRooms;
  private long updatedAt;
  private String email;
  private String nickname;
  private String username;
  private String password;
  private LocalDate birthDate;
  private boolean isSubscribedToNewsletter;
  private String phoneNumber;
  private Status status;
  private String avatarUrl;
  private String bio;
  private boolean isVerified;
  private boolean isDeactivated;
  private boolean isBanned;

  public User(
      String email,
      String username,
      String password,
      LocalDate birthDate,
      boolean isSubscribedToNewsletter,
      String nickname) {
    this.id = UUID.randomUUID();
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = this.createdAt;

    this.email = email;
    this.username = username;
    this.password = password;
    this.birthDate = birthDate;
    this.isSubscribedToNewsletter = isSubscribedToNewsletter;
    this.nickname = nickname;

    this.friends = new HashSet<>();
    this.servers = new HashSet<>();
    this.chatRooms = new HashSet<>();
    this.status = Status.OFFLINE;
  }

  public UUID getId() {
    return id;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public long getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(long updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public boolean isSubscribedToNewsletter() {
    return isSubscribedToNewsletter;
  }

  public void setSubscribedToNewsletter(boolean subscribedToNewsletter) {
    isSubscribedToNewsletter = subscribedToNewsletter;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public Set<UUID> getFriends() {
    return Collections.unmodifiableSet(friends);
  }

  public void addFriend(UUID friendId) {
    friends.add(friendId);
  }

  public void removeFriend(UUID friendId) {
    friends.remove(friendId);
  }

  public void clearFriends() {
    friends.clear();
  }

  public Set<UUID> getServers() {
    return Collections.unmodifiableSet(servers);
  }

  public void addServer(UUID serverId) {
    servers.add(serverId);
  }

  public void removeServer(UUID serverId) {
    servers.remove(serverId);
  }

  public void clearServers() {
    servers.clear();
  }

  public Set<UUID> getChatRooms() {
    return Collections.unmodifiableSet(chatRooms);
  }

  public void addChatRoom(UUID chatRoomId) {
    chatRooms.add(chatRoomId);
  }

  public void removeChatRoom(UUID chatRoomId) {
    chatRooms.remove(chatRoomId);
  }

  public void clearChatRooms() {
    chatRooms.clear();
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public boolean isVerified() {
    return isVerified;
  }

  public void setVerified(boolean verified) {
    isVerified = verified;
  }

  public boolean isDeactivated() {
    return isDeactivated;
  }

  public void setDeactivated(boolean deactivated) {
    isDeactivated = deactivated;
  }

  public boolean isBanned() {
    return isBanned;
  }

  public void setBanned(boolean banned) {
    isBanned = banned;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "User{"
        + "email='"
        + email
        + '\''
        + ", nickname='"
        + nickname
        + '\''
        + ", username='"
        + username
        + '\''
        + ", password='"
        + password
        + '\''
        + ", birthDate="
        + birthDate
        + ", isSubscribedToNewsletter="
        + isSubscribedToNewsletter
        + ", phoneNumber='"
        + phoneNumber
        + '\''
        + ", friends="
        + friends
        + ", servers="
        + servers
        + ", chatRooms="
        + chatRooms
        + ", status="
        + status
        + ", avatarUrl='"
        + avatarUrl
        + '\''
        + ", bio='"
        + bio
        + '\''
        + ", isVerified="
        + isVerified
        + ", isDeactivated="
        + isDeactivated
        + ", isBanned="
        + isBanned
        + '}';
  }
}
