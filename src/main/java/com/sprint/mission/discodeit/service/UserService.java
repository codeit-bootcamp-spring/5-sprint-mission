package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.entity.Guild;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.enums.UserStatusType;
import com.sprint.mission.discodeit.dto.request.UserRegisterRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateEmailRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdatePasswordRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateProfileImageRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateProfileSettingsRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateUsernameRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.exception.DuplicateResourceException;
import com.sprint.mission.discodeit.repository.FriendRequestRepository;
import com.sprint.mission.discodeit.repository.GuildRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static com.sprint.mission.discodeit.mapper.UserMapper.toUserResponse;

@Service
@RequiredArgsConstructor
@Profile({"test", "dev"})
public class UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final GuildRepository guildRepository;
    private final BinaryContentService binaryContentService;

    public UserResponse toResponse(User user) {
        UserStatus userStatus = userStatusRepository.getOrThrowByUserId(user.getId());
        return toUserResponse(user, userStatus.getType());
    }

    protected void update(UUID id, Consumer<User> updater) {
        User entity = userRepository.getOrThrow(id);
        updater.accept(entity);
        userRepository.save(entity);
    }

    @Transactional
    public UserResponse register(UserRegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) throw new DuplicateResourceException("이미 사용 중인 이메일입니다.");
        if (userRepository.existsByUsername(req.username())) throw new DuplicateResourceException("이미 사용 중인 사용자명입니다.");

        User user = new User(
                req.email(),
                req.username(),
                req.password(),
                req.birthDate(),
                req.subscribedToNewsletter(),
                req.globalName()
        );
        userRepository.save(user);

        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        return toUserResponse(user, UserStatusType.OFFLINE);
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
        Objects.requireNonNull(userId, "유저를 찾을 수 없습니다.");
        userRepository.getOrThrow(userId);
        update(userId, User::deactivate);
    }

    public void deleteAccount(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        friendRequestRepository.softDeleteAllByUserId(userId);

        User user = userRepository.getOrThrow(userId);

        for (UUID guildId : new HashSet<>(user.getGuildIds())) {
            Guild guild = guildRepository.getOrThrow(guildId);
            if (guild.isOwner(userId)) guildRepository.softDeleteById(guildId);
        }

        userRepository.softDeleteById(userId);
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

    public void updateEmail(UUID userId, UserUpdateEmailRequest req) {
        Objects.requireNonNull(userId, "userId must not be null.");
        Objects.requireNonNull(req, "req must not be null.");
        String email = req.email().orElseThrow(() -> new IllegalArgumentException("이메일을 입력해주세요."));
        User user = userRepository.getOrThrow(userId);
        if (user.getEmail().equals(email)) throw new IllegalArgumentException("기존과 동일한 이메일입니다.");
        if (userRepository.findByEmail(email).isPresent()) throw new IllegalArgumentException("중복된 이메일이 존재합니다.");
        update(userId, u -> u.setEmail(email));
    }

    public void updateUsername(UUID userId, UserUpdateUsernameRequest req) {
        String username = req.username().orElseThrow(() -> new IllegalArgumentException("사용자명을 입력해주세요."));
        User user = userRepository.getOrThrow(userId);
        if (user.getUsername().equals(username)) throw new IllegalArgumentException("기존과 동일한 사용자명입니다.");
        if (userRepository.findByUsername(username).isPresent()) throw new IllegalArgumentException("중복된 사용자명이 존재합니다.");
        update(userId, u -> u.setUsername(username));
    }

    public void updatePassword(UUID userId, UserUpdatePasswordRequest req) {
        String password = req.password().orElseThrow(() -> new IllegalArgumentException("비밀번호를 입력해주세요."));
        if (userRepository.getOrThrow(userId).checkPassword(password))
            throw new IllegalArgumentException("동일한 비밀번호입니다.");
        update(userId, u -> u.setPassword(password));
    }

    public List<UserResponse> getFriends(UUID userId) {
        Set<UUID> ids = userRepository.getOrThrow(userId).getFriendIds();
        return userRepository.findAllByIds(ids).stream()
                .map(this::toResponse)
                .toList();
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

    public void joinChatRoom(UUID userId, UUID channelId) {
        update(userId, u -> u.joinChannel(channelId));
    }

    public void leaveChatRoom(UUID userId, UUID channelId) {
        update(userId, u -> u.leaveChannel(channelId));
    }

    public void hardDeleteAccount(UUID userId) {
        userRepository.hardDeleteById(userId);
    }
}
