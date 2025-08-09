package com.sprint.mission.discodeit.domain.entitydev;

import com.sprint.mission.discodeit.domain.enums.user.Status;
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
public class DevUser extends DevBaseEntity {

    private String email;

    @Getter(AccessLevel.NONE)
    private String password;
    private String username;
    private String globalName;
    private LocalDate birthDate;
    private boolean subscribedToNewsletter;
    private String avatar;
    private String bio;
    private String phoneNumber;
    private Status status = Status.OFFLINE;
    private boolean verified;
    private boolean deactivated;
    private boolean banned;

    private final Set<UUID> friends = new HashSet<>();
    private final Set<UUID> guilds = new HashSet<>();
    private final Set<UUID> chatRooms = new HashSet<>();

    public DevUser(String email, String username, String password, LocalDate birthDate, boolean subscribedToNewsletter, String globalName) {
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

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = Objects.requireNonNull(birthDate, "birthDate must not be null");
        touch();
    }

    public void setStatus(Status status) {
        this.status = Objects.requireNonNullElse(status, Status.OFFLINE);
        touch();
    }

    public void setAvatar(String avatar) {
        String normalizedAvatar = normalizeString(avatar);
        this.avatar = normalizedAvatar.isBlank() ? null : Validators.validateUri(normalizedAvatar);
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

    public Set<UUID> getFriends() {
        return Collections.unmodifiableSet(friends);
    }

    public void addFriend(UUID friend) {
        Objects.requireNonNull(friend, "Friend id must not be null");
        if (friend.equals(getId())) throw new IllegalArgumentException("Cannot add self as friend");
        if (friends.add(friend)) touch();
    }

    public void removeFriend(UUID friend) {
        Objects.requireNonNull(friend, "Friend id must not be null");
        if (friends.remove(friend)) touch();
    }

    public boolean isFriend(UUID friend) {
        Objects.requireNonNull(friend, "Friend id must not be null");
        return friends.contains(friend);
    }

    public Set<UUID> getGuilds() {
        return Collections.unmodifiableSet(guilds);
    }

    public void joinGuild(UUID guild) {
        Objects.requireNonNull(guild, "Guild id must not be null");
        if (guilds.add(guild)) touch();
    }

    public void leaveGuild(UUID guild) {
        Objects.requireNonNull(guild, "Guild id must not be null");
        if (guilds.remove(guild)) touch();
    }

    public boolean isMemberOfGuild(UUID guild) {
        Objects.requireNonNull(guild, "Guild id must not be null");
        return guilds.contains(guild);
    }

    public Set<UUID> getChatRooms() {
        return Collections.unmodifiableSet(chatRooms);
    }

    public void joinChatRoom(UUID chatRoom) {
        Objects.requireNonNull(chatRoom, "ChatRoom id must not be null");
        if (chatRooms.add(chatRoom)) touch();
    }

    public void leaveChatRoom(UUID chatRoom) {
        Objects.requireNonNull(chatRoom, "ChatRoom id must not be null");
        if (chatRooms.remove(chatRoom)) touch();
    }

    public boolean isMemberOfChatRoom(UUID chatRoom) {
        Objects.requireNonNull(chatRoom, "ChatRoom id must not be null");
        return chatRooms.contains(chatRoom);
    }

    @Override
    public String toString() {
        return String.format("DevUser[id=%s, email=%s, username=%s, status=%s, isActive=%s]",
                getId(), email, username, status, isActive());
    }
}