package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.user.Status;
import com.sprint.mission.discodeit.utility.Validators;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.sprint.mission.discodeit.utility.StringUtil.normalizeString;

public class User extends BaseEntity {
    private String email;
    private String globalName;
    private String username;
    private String password;
    private LocalDate birthDate;
    private Boolean subscribedToNewsletter;
    private Status status;
    private String phoneNumber;
    private String avatar;
    private String bio;
    private Boolean verified;
    private Boolean deactivated;
    private Boolean banned;
    private final Set<UUID> friends = new HashSet<>();
    private final Set<UUID> guilds = new HashSet<>();
    private final Set<UUID> chatRooms = new HashSet<>();

    public User(String email, String username, String password, LocalDate birthDate, Boolean subscribedToNewsletter, String globalName) {
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
        this.verified = false;
        this.deactivated = false;
        this.banned = false;
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

    public Boolean isSubscribedToNewsletter() {
        return subscribedToNewsletter;
    }

    public void setSubscribedToNewsletter(Boolean subscribedToNewsletter) {
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

    public Boolean isVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(Boolean deactivated) {
        this.deactivated = deactivated;
    }

    public Boolean isBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
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
        return "User{" + "email='" + email + '\'' + ", globalName='" + globalName + '\'' + ", username='" + username + '\'' + ", status=" + status + ", deactivated=" + deactivated + ", banned=" + banned + '}';
    }
}
