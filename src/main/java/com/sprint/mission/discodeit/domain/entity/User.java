package com.sprint.mission.discodeit.domain.entity;

import com.sprint.mission.discodeit.util.Validators;
import lombok.AccessLevel;
import lombok.Getter;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.sprint.mission.discodeit.util.StringUtil.normalizeString;

@Getter
public class User extends BaseEntity {

    private String email;

    @Getter(AccessLevel.NONE)
    private String password;
    private String username;
    private String globalName;
    private LocalDate birthDate;
    private boolean subscribedToNewsletter;
    private String bio;
    private String phoneNumber;
    private boolean verified;
    private boolean deactivated;
    private boolean banned;

    private UUID profileId;
    private final Set<UUID> friendIds = new HashSet<>();
    private final Set<UUID> guildIds = new HashSet<>();
    private final Set<UUID> chatRoomIds = new HashSet<>();

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
        setSubscribedToNewsletter(subscribedToNewsletter);
        setGlobalName(globalName);
    }

    public void setEmail(String email) {
        this.email = Validators.validateEmail(email);
        touch();
    }

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(Validators.validatePassword(password), BCrypt.gensalt());
        touch();
    }

    public boolean checkPassword(String password) {
        return password != null && BCrypt.checkpw(password, this.password);
    }

    public void setUsername(String username) {
        this.username = Validators.validateUsername(username);
        touch();
    }

    public void setGlobalName(String globalName) {
        String normalized = normalizeString(globalName);
        this.globalName = normalized.isBlank()
                ? this.username
                : Validators.validateGlobalName(normalized);
        touch();
    }

    public void setProfileId(UUID profileId) {
        this.profileId = Objects.requireNonNull(profileId, "profileId must not be null");
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = Objects.requireNonNull(birthDate, "birthDate must not be null");
        touch();
    }

    public void setBio(String bio) {
        this.bio = Validators.validateBio(bio);
        touch();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = Validators.validatePhoneNumber(phoneNumber);
        touch();
    }

    public void setSubscribedToNewsletter(boolean subscribedToNewsletter) {
        this.subscribedToNewsletter = subscribedToNewsletter;
        touch();
    }

    public void verify() {
        if (!this.verified) {
            this.verified = true;
            touch();
        }
    }

    public void unverify() {
        if (this.verified) {
            this.verified = false;
            touch();
        }
    }

    public void deactivate() {
        if (!this.deactivated) {
            this.deactivated = true;
            touch();
        }
    }

    public void activate() {
        if (this.deactivated) {
            this.deactivated = false;
            touch();
        }
    }

    public void ban() {
        if (!this.banned) {
            this.banned = true;
            touch();
        }
    }

    public void unban() {
        if (this.banned) {
            this.banned = false;
            touch();
        }
    }

    public boolean isActive() {
        return !(deactivated || banned || isDeleted());
    }

    public Set<UUID> getFriendIds() {
        return Collections.unmodifiableSet(friendIds);
    }

    public void addFriend(UUID friend) {
        Objects.requireNonNull(friend, "Friend id must not be null");
        if (friend.equals(getId())) throw new IllegalArgumentException("Cannot add self as friend");
        if (friendIds.add(friend)) touch();
    }

    public void removeFriend(UUID friend) {
        Objects.requireNonNull(friend, "Friend id must not be null");
        if (friendIds.remove(friend)) touch();
    }

    public boolean isFriend(UUID friend) {
        Objects.requireNonNull(friend, "Friend id must not be null");
        return friendIds.contains(friend);
    }

    public Set<UUID> getGuildIds() {
        return Collections.unmodifiableSet(guildIds);
    }

    public void joinGuild(UUID guild) {
        Objects.requireNonNull(guild, "Guild id must not be null");
        if (guildIds.add(guild)) touch();
    }

    public void leaveGuild(UUID guild) {
        Objects.requireNonNull(guild, "Guild id must not be null");
        if (guildIds.remove(guild)) touch();
    }

    public boolean isMemberOfGuild(UUID guild) {
        Objects.requireNonNull(guild, "Guild id must not be null");
        return guildIds.contains(guild);
    }

    public Set<UUID> getChatRoomIds() {
        return Collections.unmodifiableSet(chatRoomIds);
    }

    public void joinChatRoom(UUID chatRoom) {
        Objects.requireNonNull(chatRoom, "ChatRoom id must not be null");
        if (chatRoomIds.add(chatRoom)) touch();
    }

    public void leaveChatRoom(UUID chatRoom) {
        Objects.requireNonNull(chatRoom, "ChatRoom id must not be null");
        if (chatRoomIds.remove(chatRoom)) touch();
    }

    public boolean isMemberOfChatRoom(UUID chatRoom) {
        Objects.requireNonNull(chatRoom, "ChatRoom id must not be null");
        return chatRoomIds.contains(chatRoom);
    }

    @Override
    public String toString() {
        return String.format("User[id=%s, email=%s, username=%s, isActive=%s]",
                getId(), email, username, isActive());
    }
}
