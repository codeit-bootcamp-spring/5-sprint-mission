package com.sprint.mission.discodeit.entity;


import static com.sprint.mission.discodeit.utility.StringUtil.normalizeString;

import com.sprint.mission.discodeit.enums.user.Status;
import com.sprint.mission.discodeit.utility.Validators;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class User extends BaseEntity {
  private String email;
  private String globalName;
  private String username;
  private String password;
  private LocalDate birthDate;
  private boolean subscribedToNewsletter;
  private Status status;
  private String phoneNumber;
  private String avatar;
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
      String globalName) {
    setEmail(email);
    setPassword(password);
    setUsername(username);
    setBirthDate(birthDate);
    setGlobalName(globalName);
    setSubscribedToNewsletter(subscribedToNewsletter);
    this.status = Status.OFFLINE;
    this.phoneNumber = "";
    this.avatar = "";
    this.bio = "";
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = Validators.validateEmail(email);
  }

  public String getGlobalName() {
    return globalName;
  }

  public void setGlobalName(String globalName) {
    String normalizedGlobalName = normalizeString(globalName);
    if (normalizedGlobalName.isBlank()) {
      this.globalName = this.username;
    } else {
      this.globalName = Validators.validateGlobalName(normalizedGlobalName);
    }
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = Validators.validateUsername(username);
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = BCrypt.hashpw(Validators.validatePassword(password), BCrypt.gensalt());
  }

  public boolean checkPassword(String plainPassword) {
    return BCrypt.checkpw(plainPassword, this.password);
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(LocalDate birthDate) {
    if (birthDate == null) {
      throw new IllegalArgumentException("birthDate must not be null");
    }
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
    this.phoneNumber = Validators.validatePhoneNumber(phoneNumber);
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    String normalizedAvatar = normalizeString(avatar);
    if (normalizedAvatar.isBlank()) {
      this.avatar = "";
    }
    this.avatar = Validators.validateUri(avatar);
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = Validators.validateBio(bio);
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

  public boolean isFriend(UUID friendId) {
    if (friendId == null) {
      throw new IllegalArgumentException("Friend ID must not be null.");
    }
    return friends.contains(friendId);
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

  public boolean isMemberOfGuild(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Guild ID must not be null.");
    }
    return guilds.contains(id);
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

  public boolean isMemberOfChatRoom(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("ChatRoom ID must not be null");
    }
    return chatRooms.contains(id);
  }

  public boolean isActive() {
    return !deactivated && !banned && !isDeleted();
  }

  @Override
  public String toString() {
    return "User{"
        + "email='"
        + email
        + '\''
        + ", globalName='"
        + globalName
        + '\''
        + ", username='"
        + username
        + '\''
        + ", status="
        + status
        + ", deactivated="
        + deactivated
        + ", banned="
        + banned
        + '}';
  }
}
