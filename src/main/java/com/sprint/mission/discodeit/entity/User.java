package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.user.Status;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User extends AbstractBaseEntity {
  private final long createdAt;
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
  private final Set<UUID> friends;
  private final Set<UUID> guilds;
  private final Set<UUID> chatRooms;

  public User(
      String email,
      String username,
      String password,
      LocalDate birthDate,
      boolean isSubscribedToNewsletter,
      String nickname) {
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = this.createdAt;

    this.email = email;
    this.username = username;
    this.password = password;
    this.birthDate = birthDate;
    this.isSubscribedToNewsletter = isSubscribedToNewsletter;
    this.nickname = nickname;

    this.friends = new HashSet<>();
    this.guilds = new HashSet<>();
    this.chatRooms = new HashSet<>();
    this.status = Status.OFFLINE;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public long getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(long updatedAt) {
    if (this.updatedAt < updatedAt) {
      this.updatedAt = updatedAt;
    }
  }

  public String getEmail() {
    if (email == null) {
      return "";
    }
    return email;
  }

  public void setEmail(String email) {
    if (email != null) {
      this.email = email;
    }
  }

  public String getNickname() {
    if (nickname == null) {
      return "";
    }
    return nickname;
  }

  public void setNickname(String nickname) {
    if (nickname == null) {
      this.nickname = "";
      return;
    }
    this.nickname = nickname;
  }

  public String getUsername() {
    if (username == null) {
      return "";
    }
    return username;
  }

  public void setUsername(String username) {
    if (username != null) {
      this.username = username;
    }
  }

  public String getPassword() {
    if (password == null) {
      return "";
    }
    return password;
  }

  public void setPassword(String password) {
    if (password != null) {
      this.password = password;
    }
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
    if (phoneNumber == null) {
      return "";
    }
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    if (phoneNumber == null) {
      this.phoneNumber = "";
      return;
    }
    this.phoneNumber = phoneNumber;
  }

  public Status getStatus() {
    if (status == null) {
      return Status.OFFLINE;
    }
    return status;
  }

  public void setStatus(Status status) {
    if (status == null) {
      this.status = Status.OFFLINE;
      return;
    }
    this.status = status;
  }

  public String getAvatarUrl() {
    if (avatarUrl == null) {
      return "";
    }
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    if (avatarUrl == null) {
      this.avatarUrl = "";
      return;
    }
    this.avatarUrl = avatarUrl;
  }

  public String getBio() {
    if (bio == null) {
      return "";
    }
    return bio;
  }

  public void setBio(String bio) {
    if (bio == null) {
      this.bio = "";
      return;
    }
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

  public Set<UUID> getGuilds() {
    return Collections.unmodifiableSet(guilds);
  }

  public void addGuild(UUID guildId) {
    guilds.add(guildId);
  }

  public void removeGuild(UUID guildId) {
    guilds.remove(guildId);
  }

  public void clearGuilds() {
    guilds.clear();
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

  @Override
  public String toString() {
    return "User{"
        + "id="
        + this.getId()
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + ", email='"
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
        + ", friends="
        + friends
        + ", guilds="
        + guilds
        + ", chatRooms="
        + chatRooms
        + '}';
  }
}
