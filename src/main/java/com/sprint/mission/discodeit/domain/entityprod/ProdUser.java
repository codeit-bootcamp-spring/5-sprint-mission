package com.sprint.mission.discodeit.domain.entityprod;

import com.sprint.mission.discodeit.domain.entityprod.guild.ProdGuild;
import com.sprint.mission.discodeit.util.Validators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.sprint.mission.discodeit.util.StringUtil.normalizeString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email"),
                @Index(name = "idx_user_username", columnList = "username"),
                @Index(name = "idx_user_global_name", columnList = "globalName")
        }
)
public class ProdUser extends ProdBaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Getter(AccessLevel.NONE)
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String globalName;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Setter
    @Column(nullable = false)
    private boolean subscribedToNewsletter;

    @Column
    private String bio;

    @Column
    private String phoneNumber;

    @Column(nullable = false)
    private boolean verified;

    @Column(nullable = false)
    private boolean deactivated;

    @Column(nullable = false)
    private boolean banned;

    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uq_user_friends_user_friend",
                    columnNames = {"user_id", "friend_id"}
            )
    )
    private Set<ProdUser> friends = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_guilds",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "guild_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uq_user_guilds_user_guild",
                    columnNames = {"user_id", "guild_id"}
            )
    )
    private Set<ProdGuild> guilds = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_chat_rooms",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "chat_room_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uq_user_chat_rooms_user_chat_room",
                    columnNames = {"user_id", "chat_room_id"}
            )
    )
    private Set<ProdChatRoom> chatRooms = new HashSet<>();

    public ProdUser(
            String email,
            String username,
            String password,
            LocalDate birthDate,
            boolean subscribedToNewsletter,
            String globalName) {
        setEmail(email);
        setUsername(username);
        setPassword(password);
        setBirthDate(birthDate);
        setSubscribedToNewsletter(subscribedToNewsletter);
        setGlobalName(globalName);
    }

    public void setEmail(String email) {
        this.email = Validators.validateEmail(email);
    }

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(Validators.validatePassword(password), BCrypt.gensalt());
    }

    public boolean checkPassword(String password) {
        return BCrypt.checkpw(password, this.password);
    }

    public void setUsername(String username) {
        this.username = Validators.validateUsername(username);
    }

    public void setGlobalName(String globalName) {
        String normalized = normalizeString(globalName);
        this.globalName = normalized.isBlank()
                ? this.username
                : Validators.validateGlobalName(normalized);
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = Objects.requireNonNull(birthDate, "birthDate must not be null");
    }

    public void setBio(String bio) {
        this.bio = Validators.validateBio(bio);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = Validators.validatePhoneNumber(phoneNumber);
    }

    public void verify() {
        this.verified = true;
    }

    public void unverify() {
        this.verified = false;
    }

    public void deactivate() {
        this.deactivated = true;
    }

    public void activate() {
        this.deactivated = false;
    }

    public void ban() {
        this.banned = true;
    }

    public void unban() {
        this.banned = false;
    }

    public boolean isActive() {
        return !(deactivated || banned || isDeleted());
    }

    public Set<ProdUser> getFriends() {
        return Collections.unmodifiableSet(friends);
    }

    public void addFriend(ProdUser friend) {
        Objects.requireNonNull(friend, "friend must not be null");
        if (friend.equals(this)) throw new IllegalArgumentException("Cannot add self as friend");
        friends.add(friend);
    }

    public void removeFriend(ProdUser friend) {
        Objects.requireNonNull(friend, "friend must not be null");
        friends.remove(friend);
    }

    public boolean isFriend(ProdUser friend) {
        Objects.requireNonNull(friend, "friend must not be null");
        return friends.contains(friend);
    }

    public Set<ProdGuild> getGuilds() {
        return Collections.unmodifiableSet(guilds);
    }

    public void joinGuild(ProdGuild jpaGuild) {
        Objects.requireNonNull(jpaGuild, "guild must not be null");
        guilds.add(jpaGuild);
    }

    public void leaveGuild(ProdGuild jpaGuild) {
        Objects.requireNonNull(jpaGuild, "guild must not be null");
        guilds.remove(jpaGuild);
    }

    public boolean isMemberOfGuild(ProdGuild jpaGuild) {
        Objects.requireNonNull(jpaGuild, "guild must not be null");
        return guilds.contains(jpaGuild);
    }

    public Set<ProdChatRoom> getChatRooms() {
        return Collections.unmodifiableSet(chatRooms);
    }

    public void joinChatRoom(ProdChatRoom jpaChatRoom) {
        Objects.requireNonNull(jpaChatRoom, "chatRoom must not be null");
        chatRooms.add(jpaChatRoom);
    }

    public void leaveChatRoom(ProdChatRoom jpaChatRoom) {
        Objects.requireNonNull(jpaChatRoom, "chatRoom must not be null");
        chatRooms.remove(jpaChatRoom);
    }

    public boolean isMemberOfChatRoom(ProdChatRoom jpaChatRoom) {
        Objects.requireNonNull(jpaChatRoom, "chatRoom must not be null");
        return chatRooms.contains(jpaChatRoom);
    }

    @Override
    public String toString() {
        return String.format("User[id=%s, email=%s, username=%s, isActive=%s]",
                getId(), email, username, isActive());
    }
}
