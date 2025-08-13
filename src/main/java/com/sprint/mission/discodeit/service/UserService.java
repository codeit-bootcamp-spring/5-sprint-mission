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
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.FriendRequestRepository;
import com.sprint.mission.discodeit.repository.GuildRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static com.sprint.mission.discodeit.mapper.UserMapper.toUserResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final GuildRepository guildRepository;
    private final BinaryContentRepository binaryContentRepository;

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
        return userRepository.findById(userId)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다: " + userId));
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deactivateAccount(UUID userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다: " + userId));
        update(userId, User::deactivate);
    }

    @Transactional
    public void deleteAccount(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다: " + userId));

        guildRepository.findGuildsOwnedByUser(userId).forEach(g -> guildRepository.softDeleteById(g.getId()));

        friendRequestRepository.softDeleteAllByUserId(userId);

        userRepository.softDeleteById(userId);
    }

    @Transactional
    public void updateProfileSettings(UUID userId, UserUpdateProfileSettingsRequest req) {
        update(userId, u -> {
            u.setGlobalName(req.globalName());
            u.setBio(req.bio());
        });
    }

    @Transactional
    public void updateProfileImage(UUID userId, UserUpdateProfileImageRequest req) {
        binaryContentRepository.findById(req.profileId()).orElseThrow(() -> new NotFoundException("프로필 이미지를 찾을 수 없습니다."));
        update(userId, u -> u.setProfileId(req.profileId()));
    }

    @Transactional
    public void updateEmail(UUID userId, UserUpdateEmailRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));
        if (user.getEmail().equals(req.email())) throw new IllegalArgumentException("기존과 동일한 이메일입니다.");
        if (userRepository.findByEmail(req.email()).isPresent())
            throw new DuplicateResourceException("중복된 이메일이 존재합니다.");
        update(userId, u -> u.setEmail(req.email()));
    }

    @Transactional
    public void updateUsername(UUID userId, UserUpdateUsernameRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));
        if (user.getUsername().equals(req.username())) throw new IllegalArgumentException("기존과 동일한 사용자명입니다.");
        if (userRepository.findByUsername(req.username()).isPresent())
            throw new DuplicateResourceException("중복된 사용자명이 존재합니다.");
        update(userId, u -> u.setUsername(req.username()));
    }

    @Transactional
    public void updatePassword(UUID userId, UserUpdatePasswordRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));
        if (user.checkPassword(req.password())) throw new DuplicateResourceException("동일한 비밀번호입니다.");
        update(userId, u -> u.setPassword(req.password()));
    }

    public List<UserResponse> getFriends(UUID userId) {
        Set<UUID> ids = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("유저를 찾을 수 없습니다.")
        ).getFriendIds();
        return userRepository.findAllByIds(ids).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void removeFriend(UUID userId, UUID friendId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));
        userRepository.findById(friendId).orElseThrow(() -> new NotFoundException("상대 유저를 찾을 수 없습니다."));
        update(userId, u -> u.removeFriend(friendId));
        update(friendId, u -> u.removeFriend(userId));
    }

    public List<Guild> getGuilds(UUID userId) {
        Set<UUID> ids = userRepository.getOrThrow(userId).getGuildIds();
        return guildRepository.findAllByIds(ids);
    }

    @Transactional
    public void joinGuild(UUID userId, UUID guildId) {
        Guild guild = guildRepository.getOrThrow(guildId);
        update(userId, u -> u.joinGuild(guildId));
        guild.addUser(userId);
        guildRepository.save(guild);
    }

    @Transactional
    public void leaveGuild(UUID userId, UUID guildId) {
        Guild guild = guildRepository.getOrThrow(guildId);
        if (guild.isOwner(userId))
            throw new IllegalArgumentException("Cannot leave the guild. Transfer ownership first.");
        update(userId, u -> u.leaveGuild(guildId));
        guild.removeUser(userId);
        guildRepository.save(guild);
    }

    @Transactional
    public void joinChannel(UUID userId, UUID channelId) {
        update(userId, u -> u.joinChannel(channelId));
    }

    @Transactional
    public void leaveChannel(UUID userId, UUID channelId) {
        update(userId, u -> u.leaveChannel(channelId));
    }
}
