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
    private final Set<UUID> channelIds = new HashSet<>();

    public User(
            String email,
            String username,
            String password,
            LocalDate birthDate,
            boolean subscribedToNewsletter,
            String globalName
    ) {
        setEmail(email);
        setPassword(password);
        setUsername(username);
        setBirthDate(birthDate);
        setSubscribedToNewsletter(subscribedToNewsletter);
        setGlobalName(globalName);
    }

    public void setEmail(String email) {
        String v = Validators.validateEmail(email);
        if (!Objects.equals(this.email, v)) {
            this.email = v;
            touch();
        }
    }

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(Validators.validatePassword(password), BCrypt.gensalt());
        touch();
    }

    public boolean checkPassword(String password) {
        return password != null && BCrypt.checkpw(password, this.password);
    }

    public void setUsername(String username) {
        String v = Validators.validateUsername(username);
        if (!Objects.equals(this.username, v)) {
            this.username = v;
            touch();
        }
    }

    public void setGlobalName(String globalName) {
        String normalized = normalizeString(globalName);
        String v = normalized.isBlank() ? this.username : Validators.validateGlobalName(normalized);
        if (!Objects.equals(this.globalName, v)) {
            this.globalName = v;
            touch();
        }
    }

    public void setBirthDate(LocalDate birthDate) {
        Objects.requireNonNull(birthDate, "birthDate must not be null");
        if (!Objects.equals(this.birthDate, birthDate)) {
            this.birthDate = birthDate;
            touch();
        }
    }

    public void setBio(String bio) {
        String v = Validators.validateBio(bio);
        if (!Objects.equals(this.bio, v)) {
            this.bio = v;
            touch();
        }
    }

    public void setPhoneNumber(String phoneNumber) {
        String v = Validators.validatePhoneNumber(phoneNumber);
        if (!Objects.equals(this.phoneNumber, v)) {
            this.phoneNumber = v;
            touch();
        }
    }

    public void setSubscribedToNewsletter(boolean subscribedToNewsletter) {
        if (this.subscribedToNewsletter != subscribedToNewsletter) {
            this.subscribedToNewsletter = subscribedToNewsletter;
            touch();
        }
    }

    public void setProfileId(UUID profileId) {
        Objects.requireNonNull(profileId, "profileId must not be null");
        if (!Objects.equals(this.profileId, profileId)) {
            this.profileId = profileId;
            touch();
        }
    }

    public void clearProfile() {
        if (this.profileId != null) {
            this.profileId = null;
            touch();
        }
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
        Objects.requireNonNull(friend, "friendId must not be null");
        if (friend.equals(getId())) throw new IllegalArgumentException("Cannot add self as friend");
        if (friendIds.add(friend)) touch();
    }

    public void removeFriend(UUID friend) {
        Objects.requireNonNull(friend, "friendId must not be null");
        if (friendIds.remove(friend)) touch();
    }

    public boolean isFriend(UUID friend) {
        Objects.requireNonNull(friend, "friendId must not be null");
        return friendIds.contains(friend);
    }

    public Set<UUID> getGuildIds() {
        return Collections.unmodifiableSet(guildIds);
    }

    public void joinGuild(UUID guildId) {
        Objects.requireNonNull(guildId, "guildId must not be null");
        if (guildIds.add(guildId)) touch();
    }

    public void leaveGuild(UUID guildId) {
        Objects.requireNonNull(guildId, "guildId must not be null");
        if (guildIds.remove(guildId)) touch();
    }

    public boolean isMemberOfGuild(UUID guildId) {
        Objects.requireNonNull(guildId, "guildId must not be null");
        return guildIds.contains(guildId);
    }

    public Set<UUID> getChannelIds() {
        return Collections.unmodifiableSet(channelIds);
    }

    public void joinChannel(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId must not be null");
        if (channelIds.add(channelId)) touch();
    }

    public void leaveChannel(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId must not be null");
        if (channelIds.remove(channelId)) touch();
    }

    public boolean isMemberOfChannel(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId must not be null");
        return channelIds.contains(channelId);
    }

    @Override
    public String toString() {
        return "User[id=%s, email=%s, username=%s, isActive=%s]"
                .formatted(getId(), email, username, isActive());
    }
}
