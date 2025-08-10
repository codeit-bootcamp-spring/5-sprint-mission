package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.entity.guild.Guild;
import com.sprint.mission.discodeit.repository.FriendRequestRepository;
import com.sprint.mission.discodeit.repository.GuildRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Profile({"test", "dev"})
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final GuildRepository guildRepository;

    protected void update(UUID id, Consumer<User> updater) {
        User entity = userRepository.getOrThrow(id);
        updater.accept(entity);
        userRepository.save(entity);
    }

    @Override
    public User register(String email,
                         String username,
                         String password,
                         LocalDate birthDate,
                         boolean subscribedToNewsletter,
                         String globalName) {

        String e = Validators.validateEmail(email);
        String u = Validators.validateUsername(username);
        Objects.requireNonNull(birthDate, "birthDate must not be null");
        String p = Validators.validatePassword(password);
        if (userRepository.existsByEmail(e)) throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        if (userRepository.existsByUsername(u)) throw new IllegalArgumentException("이미 사용 중인 사용자명입니다.");

        User user = userRepository.save(new User(e, u, p, birthDate, subscribedToNewsletter, globalName));
        userStatusRepository.save(new UserStatus(user.getId()));
        return user;
    }

    // @Override
    // public UserResponse register() {
    //     return null;
    // }
    //
    // @Override
    // public UserResponse findById(UUID userId) {
    //     return null;
    // }
    //
    // @Override
    // public List<UserResponse> findAll() {
    //     return List.of();
    // }
    //
    // @Override
    // public UserResponse update(UserUpdateCommand cmd) {
    //     return null;
    // }

    @Override
    public void deactivateAccount(UUID userId) {
        update(userId, User::deactivate);
    }

    @Override
    public void reactivateAccount(UUID userId) {
        update(userId, User::activate);
    }

    @Override
    public void deleteAccount(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("userId는 null일 수 없습니다.");

        friendRequestRepository.clear(userId);

        User user = userRepository.getOrThrow(userId);
        for (UUID friendId : new HashSet<>(user.getFriendIds())) {
            update(friendId, f -> f.removeFriend(userId));
            update(userId, u -> u.removeFriend(friendId));
        }

        for (UUID guildId : new HashSet<>(user.getGuildIds())) {
            Guild guild = guildRepository.getOrThrow(guildId);
            if (guild.isOwner(userId)) guildRepository.deleteById(guildId);
            else {
                guild.removeUser(userId);
                guildRepository.save(guild);
            }
        }

        userRepository.deleteById(userId);
    }

    @Override
    public void updateEmail(UUID userId, String email) {
        User user = userRepository.getOrThrow(userId);
        if (user.getEmail().equals(email)) throw new IllegalArgumentException("기존과 동일한 이메일입니다.");
        if (userRepository.findByEmail(email).isPresent()) throw new IllegalArgumentException("중복된 이메일이 존재합니다.");
        update(userId, u -> u.setEmail(email));
    }

    @Override
    public void updateGlobalName(UUID userId, String globalName) {
        update(userId, u -> u.setGlobalName(globalName));
    }

    @Override
    public void updateUsername(UUID userId, String username) {
        if (userRepository.findByEmail(username).isPresent()) throw new IllegalArgumentException("중복된 사용자명이 존재합니다.");
        update(userId, u -> u.setUsername(username));
    }

    @Override
    public void updatePassword(UUID userId, String password) {
        if (userRepository.getOrThrow(userId).checkPassword(password))
            throw new IllegalArgumentException("동일한 비밀번호입니다.");
        update(userId, u -> u.setPassword(password));
    }

    @Override
    public void updateBirthDate(UUID userId, LocalDate birthDate) {
        if (birthDate == null) throw new IllegalArgumentException("생년월일은 필수입니다.");
        update(userId, u -> u.setBirthDate(birthDate));
    }

    @Override
    public void updateSubscribedToNewsletter(UUID userId, boolean isSubscribedToNewsletter) {
        update(userId, u -> u.setSubscribedToNewsletter(isSubscribedToNewsletter));
    }

    @Override
    public void updatePhoneNumber(UUID userId, String phoneNumber) {
        update(userId, u -> u.setPhoneNumber(phoneNumber));
    }

    @Override
    public void updateBio(UUID userId, String bio) {
        update(userId, u -> u.setBio(bio));
    }

    @Override
    public void updateVerified(UUID userId, boolean verified) {
        if (verified) update(userId, User::verify);
        else update(userId, User::unverify);
    }

    @Override
    public void updateBanned(UUID userId, boolean banned) {
        if (banned) update(userId, User::ban);
        else update(userId, User::unban);
    }

    @Override
    public List<User> getFriends(UUID userId) {
        Set<UUID> ids = userRepository.getOrThrow(userId).getFriendIds();
        return userRepository.findAllByIds(ids);
    }

    @Override
    public void addFriend(UUID userId, UUID friendId) {
        if (userId.equals(friendId)) throw new IllegalArgumentException("자기 자신에게는 친구 요청을 보낼 수 없습니다.");
        userRepository.getOrThrow(userId);
        userRepository.getOrThrow(friendId);
        update(userId, u -> u.addFriend(friendId));
        update(friendId, u -> u.addFriend(userId));
    }

    @Override
    public void removeFriend(UUID userId, UUID friendId) {
        userRepository.getOrThrow(userId);
        userRepository.getOrThrow(friendId);
        update(userId, u -> u.removeFriend(friendId));
        update(friendId, u -> u.removeFriend(userId));
    }

    @Override
    public List<Guild> getGuilds(UUID userId) {
        Set<UUID> ids = userRepository.getOrThrow(userId).getGuildIds();
        return guildRepository.findAllByIds(ids);
    }

    @Override
    public void joinGuild(UUID userId, UUID guildId) {
        Guild guild = guildRepository.getOrThrow(guildId);
        update(userId, u -> u.joinGuild(guildId));
        guild.addUser(userId);
        guildRepository.save(guild);
    }

    @Override
    public void leaveGuild(UUID userId, UUID guildId) {
        Guild guild = guildRepository.getOrThrow(guildId);
        if (guild.isOwner(userId))
            throw new IllegalArgumentException("Cannot leave the guild. Transfer ownership first.");
        update(userId, u -> u.leaveGuild(guildId));
        guild.removeUser(userId);
        guildRepository.save(guild);
    }

    @Override
    public void joinChatRoom(UUID userId, UUID chatRoomId) {
        update(userId, u -> u.joinChatRoom(chatRoomId));
    }

    @Override
    public void leaveChatRoom(UUID userId, UUID chatRoomId) {
        update(userId, u -> u.leaveChatRoom(chatRoomId));
    }

    @Override
    public void hardDeleteAccount(UUID userId) {
        userRepository.hardDeleteById(userId);
    }
}
