package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.entity.Guild;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.dto.request.UserRegisterRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateProfileImageRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateProfileSettingsRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.repository.FriendRequestRepository;
import com.sprint.mission.discodeit.repository.GuildRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
public class BasicUserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final GuildRepository guildRepository;
    private final BinaryContentService binaryContentService;

    private UserResponse toResponse(User user) {
        UserStatus userStatus = userStatusRepository.getOrThrowByUserId(user.getId());
        return new UserResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getEmail(),
                user.getUsername(),
                user.getGlobalName(),
                user.getProfileId(),
                userStatus.getStatus()
        );
    }

    protected void update(UUID id, Consumer<User> updater) {
        User entity = userRepository.getOrThrow(id);
        updater.accept(entity);
        userRepository.save(entity);
    }

    public UserResponse register(UserRegisterRequest req) {
        Objects.requireNonNull(req, "req must not be null");
        Objects.requireNonNull(req.birthDate(), "birthDate must not be null");

        String e = Validators.validateEmail(req.email());
        String u = Validators.validateUsername(req.username());
        String p = Validators.validatePassword(req.password());

        if (userRepository.existsByEmail(e)) throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        if (userRepository.existsByUsername(u)) throw new IllegalArgumentException("이미 사용 중인 사용자명입니다.");

        User saved = userRepository.save(new User(
                e, u, p, req.birthDate(), req.subscribedToNewsletter(), req.globalName()
        ));

        userStatusRepository.save(new UserStatus(saved.getId()));

        return toResponse(saved);
    }

    public UserResponse findById(UUID userId) {
        return toResponse(userRepository.getOrThrow(userId));
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public void deactivateAccount(UUID userId) {
        update(userId, User::deactivate);
    }

    public void reactivateAccount(UUID userId) {
        update(userId, User::activate);
    }

    public void deleteAccount(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("userId는 null일 수 없습니다.");

        friendRequestRepository.clear(userId);

        User user = userRepository.getOrThrow(userId);

        for (UUID guildId : new HashSet<>(user.getGuildIds())) {
            Guild guild = guildRepository.getOrThrow(guildId);
            if (guild.isOwner(userId)) guildRepository.deleteById(guildId);
        }

        userRepository.deleteById(userId);
    }

    public void updateProfileSettings(UUID userId, UserUpdateProfileSettingsRequest req) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(req, "req must not be null");
        update(userId, u -> {
            u.setGlobalName(req.globalName().orElse(null));
            u.setBio(req.bio().orElse(null));
        });
    }

    public void updateProfileImage(UUID userId, UserUpdateProfileImageRequest req) {
        Objects.requireNonNull(userId, "userId must not be null.");
        Objects.requireNonNull(req, "req must not be null.");
        binaryContentService.find(req.profileId());
        update(userId, u -> u.setProfileId(req.profileId()));
    }

    public void updateEmail(UUID userId, String email) {
        User user = userRepository.getOrThrow(userId);
        if (user.getEmail().equals(email)) throw new IllegalArgumentException("기존과 동일한 이메일입니다.");
        if (userRepository.findByEmail(email).isPresent()) throw new IllegalArgumentException("중복된 이메일이 존재합니다.");
        update(userId, u -> u.setEmail(email));
    }

    public void updateGlobalName(UUID userId, String globalName) {
        update(userId, u -> u.setGlobalName(globalName));
    }

    public void updateUsername(UUID userId, String username) {
        if (userRepository.findByEmail(username).isPresent()) throw new IllegalArgumentException("중복된 사용자명이 존재합니다.");
        update(userId, u -> u.setUsername(username));
    }

    public void updatePassword(UUID userId, String password) {
        if (userRepository.getOrThrow(userId).checkPassword(password))
            throw new IllegalArgumentException("동일한 비밀번호입니다.");
        update(userId, u -> u.setPassword(password));
    }

    public void updateBirthDate(UUID userId, LocalDate birthDate) {
        if (birthDate == null) throw new IllegalArgumentException("생년월일은 필수입니다.");
        update(userId, u -> u.setBirthDate(birthDate));
    }

    public void updateSubscribedToNewsletter(UUID userId, boolean isSubscribedToNewsletter) {
        update(userId, u -> u.setSubscribedToNewsletter(isSubscribedToNewsletter));
    }

    public void updatePhoneNumber(UUID userId, String phoneNumber) {
        update(userId, u -> u.setPhoneNumber(phoneNumber));
    }

    public void updateBio(UUID userId, String bio) {
        update(userId, u -> u.setBio(bio));
    }

    public void updateVerified(UUID userId, boolean verified) {
        if (verified) update(userId, User::verify);
        else update(userId, User::unverify);
    }

    public void updateBanned(UUID userId, boolean banned) {
        if (banned) update(userId, User::ban);
        else update(userId, User::unban);
    }

    public List<User> getFriends(UUID userId) {
        Set<UUID> ids = userRepository.getOrThrow(userId).getFriendIds();
        return userRepository.findAllByIds(ids);
    }

    public void addFriend(UUID userId, UUID friendId) {
        if (userId.equals(friendId)) throw new IllegalArgumentException("자기 자신에게는 친구 요청을 보낼 수 없습니다.");
        userRepository.getOrThrow(userId);
        userRepository.getOrThrow(friendId);
        update(userId, u -> u.addFriend(friendId));
        update(friendId, u -> u.addFriend(userId));
    }

    public void removeFriend(UUID userId, UUID friendId) {
        userRepository.getOrThrow(userId);
        userRepository.getOrThrow(friendId);
        update(userId, u -> u.removeFriend(friendId));
        update(friendId, u -> u.removeFriend(userId));
    }

    public List<Guild> getGuilds(UUID userId) {
        Set<UUID> ids = userRepository.getOrThrow(userId).getGuildIds();
        return guildRepository.findAllByIds(ids);
    }

    public void joinGuild(UUID userId, UUID guildId) {
        Guild guild = guildRepository.getOrThrow(guildId);
        update(userId, u -> u.joinGuild(guildId));
        guild.addUser(userId);
        guildRepository.save(guild);
    }

    public void leaveGuild(UUID userId, UUID guildId) {
        Guild guild = guildRepository.getOrThrow(guildId);
        if (guild.isOwner(userId))
            throw new IllegalArgumentException("Cannot leave the guild. Transfer ownership first.");
        update(userId, u -> u.leaveGuild(guildId));
        guild.removeUser(userId);
        guildRepository.save(guild);
    }

    public void joinChatRoom(UUID userId, UUID chatRoomId) {
        update(userId, u -> u.joinChatRoom(chatRoomId));
    }

    public void leaveChatRoom(UUID userId, UUID chatRoomId) {
        update(userId, u -> u.leaveChatRoom(chatRoomId));
    }

    public void hardDeleteAccount(UUID userId) {
        userRepository.hardDeleteById(userId);
    }
}
