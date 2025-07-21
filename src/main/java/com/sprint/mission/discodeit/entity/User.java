package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.user.Status;
import com.sprint.mission.discodeit.utility.StringUtil;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User extends BaseEntity {
  private String email;
  private String globalName;
  private String username;
  private String password;
  private LocalDate birthDate;
  private boolean subscribedToNewsletter;
  private Status status;
  private String phoneNumber;
  private String avatarUrl;
  private String bio;
  private boolean verified;
  private boolean deactivated;
  private boolean banned;
  private final Set<UUID> friends;
  private final Set<UUID> guilds;
  private final Set<UUID> chatRooms;

  public User(
      String email,
      String username,
      String password,
      LocalDate birthDate,
      boolean subscribedToNewsletter,
      String globalName) {
    this.email = StringUtil.normalizeString(email).toLowerCase();
    this.username = StringUtil.normalizeString(username);
    this.password = StringUtil.normalizeString(password);
    this.birthDate = birthDate;
    this.subscribedToNewsletter = subscribedToNewsletter;
    this.globalName = StringUtil.normalizeString(globalName);
    this.status = Status.OFFLINE;
    this.phoneNumber = "";
    this.avatarUrl = "";
    this.bio = "";
    this.friends = new HashSet<>();
    this.guilds = new HashSet<>();
    this.chatRooms = new HashSet<>();
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = StringUtil.normalizeString(email).toLowerCase();
  }

  public String getGlobalName() {
    return globalName;
  }

  public void setGlobalName(String globalName) {
    this.globalName = StringUtil.normalizeString(globalName);
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = StringUtil.normalizeString(username);
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = StringUtil.normalizeString(password);
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public boolean isSubscribedToNewsletter() {
    return subscribedToNewsletter;
  }

  public void setSubscribedToNewsletter(boolean subscribedToNewsletter) {
    this.subscribedToNewsletter = subscribedToNewsletter;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status != null ? status : Status.OFFLINE;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = StringUtil.normalizeString(phoneNumber);
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = StringUtil.normalizeString(avatarUrl);
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = StringUtil.normalizeString(bio);
  }

  public boolean isVerified() {
    return verified;
  }

  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  public boolean isDeactivated() {
    return deactivated;
  }

  public void setDeactivated(boolean deactivated) {
    this.deactivated = deactivated;
  }

  public boolean isBanned() {
    return banned;
  }

  public void setBanned(boolean banned) {
    this.banned = banned;
  }

  public Set<UUID> getFriends() {
    return Collections.unmodifiableSet(friends);
  }

  public void addFriend(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Friend ID must not be null");
    }
    friends.add(id);
  }

  public void removeFriend(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Friend ID must not be null");
    }
    friends.remove(id);
  }

  public Set<UUID> getGuilds() {
    return Collections.unmodifiableSet(guilds);
  }

  public void addGuild(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Guild ID must not be null");
    }
    guilds.add(id);
  }

  public void removeGuild(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Guild ID must not be null");
    }
    guilds.remove(id);
  }

  public Set<UUID> getChatRooms() {
    return Collections.unmodifiableSet(chatRooms);
  }

  public void addChatRoom(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("ChatRoom ID must not be null");
    }
    chatRooms.add(id);
  }

  public void removeChatRoom(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("ChatRoom ID must not be null");
    }
    chatRooms.remove(id);
  }

  @Override
  public String toString() {
    return "User{"
        + "id="
        + getId()
        + ", createdAt="
        + getCreatedAt()
        + ", updatedAt="
        + getUpdatedAt()
        + ", email='"
        + email
        + '\''
        + ", globalName='"
        + globalName
        + '\''
        + ", username='"
        + username
        + '\''
        + ", password='"
        + password
        + '\''
        + ", birthDate="
        + birthDate
        + ", subscribedToNewsletter="
        + subscribedToNewsletter
        + ", status="
        + status
        + ", phoneNumber='"
        + phoneNumber
        + '\''
        + ", avatarUrl='"
        + avatarUrl
        + '\''
        + ", bio='"
        + bio
        + '\''
        + ", verified="
        + verified
        + ", deactivated="
        + deactivated
        + ", banned="
        + banned
        + ", friends="
        + friends
        + ", guilds="
        + guilds
        + ", chatRooms="
        + chatRooms
        + '}';
  }
}
