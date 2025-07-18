package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.user.Status;
import java.time.LocalDate;
import java.util.*;

public class User extends AbstractBaseEntity {
  private String email;
  private String nickname;
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

  private final Set<UUID> friends = new HashSet<>();
  private final Set<UUID> guilds = new HashSet<>();
  private final Set<UUID> chatRooms = new HashSet<>();

  public User(
      String email,
      String username,
      String password,
      LocalDate birthDate,
      boolean subscribedToNewsletter,
      String nickname) {
    this.email = optionalString(email).toLowerCase();
    this.username = optionalString(username);
    this.password = optionalString(password);
    this.birthDate = birthDate;
    this.subscribedToNewsletter = subscribedToNewsletter;
    this.nickname = optionalString(nickname);
    this.status = Status.OFFLINE;
    this.phoneNumber = "";
    this.avatarUrl = "";
    this.bio = "";
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = optionalString(email).toLowerCase();
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = optionalString(nickname);
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = optionalString(username);
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = optionalString(password);
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
    this.phoneNumber = optionalString(phoneNumber);
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = optionalString(avatarUrl);
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = optionalString(bio);
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
    friends.add(id);
  }

  public void removeFriend(UUID id) {
    friends.remove(id);
  }

  public void clearFriends() {
    friends.clear();
  }

  public Set<UUID> getGuilds() {
    return Collections.unmodifiableSet(guilds);
  }

  public void addGuild(UUID id) {
    guilds.add(id);
  }

  public void removeGuild(UUID id) {
    guilds.remove(id);
  }

  public void clearGuilds() {
    guilds.clear();
  }

  public Set<UUID> getChatRooms() {
    return Collections.unmodifiableSet(chatRooms);
  }

  public void addChatRoom(UUID id) {
    chatRooms.add(id);
  }

  public void removeChatRoom(UUID id) {
    chatRooms.remove(id);
  }

  public void clearChatRooms() {
    chatRooms.clear();
  }

  private String optionalString(String input) {
    return input == null ? "" : input.strip();
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
