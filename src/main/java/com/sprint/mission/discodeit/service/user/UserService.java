package com.sprint.mission.discodeit.service.user;

import com.sprint.mission.discodeit.domain.entity.Guild;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.dto.request.user.UserRegisterRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateEmailRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdatePasswordRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateProfileImageRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateProfileSettingsRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateUsernameRequest;
import com.sprint.mission.discodeit.dto.response.user.UserRegisterResponse;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.exception.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.FriendRequestRepository;
import com.sprint.mission.discodeit.repository.GuildRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static com.sprint.mission.discodeit.mapper.UserMapper.toUserResponse;
import static com.sprint.mission.discodeit.support.StringUtil.stripToLowerCase;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final GuildRepository guildRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final PasswordEncoder passwordEncoder;

    public UserResponse toResponse(User user) {
        UserStatus userStatus = userStatusRepository.getOrThrowByUserId(user.getId());
        return toUserResponse(user, userStatus.getType());
    }

    @Transactional
    protected void update(UUID id, Consumer<User> updater) {
        User entity = userRepository.getOrThrow(id);
        updater.accept(entity);
        userRepository.save(entity);
    }

    @Transactional
    public UserRegisterResponse register(UserRegisterRequest req) {
        String email = stripToLowerCase(req.email());
        if (userRepository.existsByEmail(email)) throw new DuplicateResourceException("이미 사용 중인 이메일입니다.");
        String username = stripToLowerCase(req.username());
        if (userRepository.existsByUsername(username)) throw new DuplicateResourceException("이미 사용 중인 사용자명입니다.");

        String password = passwordEncoder.encode(req.password());

        User user = new User(
                email,
                username,
                password,
                req.birthDate(),
                req.subscribedToNewsletter(),
                req.globalName()
        );

        try {
            User saved = userRepository.save(user);

            UserStatus userStatus = new UserStatus(saved.getId());
            userStatusRepository.save(userStatus);

            return UserRegisterResponse.from(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("이미 사용 중인 이메일 또는 사용자명입니다.");
        }
    }

    public UserResponse find(UUID userId) {
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
            u.changeGlobalName(req.globalName());
            u.changeBio(req.bio());
        });
    }

    @Transactional
    public void updateProfileImage(UUID userId, UserUpdateProfileImageRequest req) {
        binaryContentRepository.findById(req.profileId()).orElseThrow(() -> new NotFoundException("프로필 이미지를 찾을 수 없습니다."));
        update(userId, u -> u.changeProfileId(req.profileId()));
    }

    @Transactional
    public void updateEmail(UUID userId, UserUpdateEmailRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));
        if (user.getEmail().equals(req.email())) throw new IllegalArgumentException("기존과 동일한 이메일입니다.");
        if (userRepository.findByEmail(req.email()).isPresent())
            throw new DuplicateResourceException("중복된 이메일이 존재합니다.");
        update(userId, u -> u.changeEmail(req.email()));
    }

    @Transactional
    public void updateUsername(UUID userId, UserUpdateUsernameRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));
        if (user.getUsername().equals(req.username())) throw new IllegalArgumentException("기존과 동일한 사용자명입니다.");
        if (userRepository.findByUsername(req.username()).isPresent())
            throw new DuplicateResourceException("중복된 사용자명이 존재합니다.");
        update(userId, u -> u.changeUsername(req.username()));
    }

    @Transactional
    public void updatePassword(UUID userId, UserUpdatePasswordRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));
        // if (user.checkPassword(req.password())) throw new DuplicateResourceException("동일한 비밀번호입니다.");
        update(userId, u -> u.changePassword(req.password()));
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
