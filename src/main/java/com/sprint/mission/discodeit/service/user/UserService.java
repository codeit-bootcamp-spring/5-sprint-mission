package com.sprint.mission.discodeit.service.user;

import com.sprint.mission.discodeit.domain.entity.Guild;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.domain.enums.UserStatusType;
import com.sprint.mission.discodeit.dto.request.user.UserRegisterRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateEmailRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdatePasswordRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdatePhoneNumberRequest;
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
import com.sprint.mission.discodeit.support.PhoneNumbers;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    private UserResponse toResponse(User user) {
        UserStatus userStatus = userStatusRepository.getOrThrowByUserId(user.getId());
        return UserResponse.from(user, userStatus.getType());
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
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) return List.of();
        Set<UUID> ids = users.stream().map(User::getId).collect(Collectors.toSet());

        Map<UUID, UserStatusType> statusMap = userStatusRepository.findAllByUserIds(ids).stream()
                .collect(Collectors.toMap(
                        UserStatus::getUserId,
                        UserStatus::getType
                ));

        return users.stream()
                .map(u -> UserResponse.from(u, statusMap.get(u.getId())))
                .toList();
    }

    public List<UserResponse> findByUsername(String username) {
        String v = stripToLowerCase(username);
        return userRepository.findByUsername(v)
                .map(this::toResponse)
                .map(List::of)
                .orElse(List.of());
    }

    public List<UserResponse> findByEmail(String email) {
        String v = stripToLowerCase(email);
        return userRepository.findByEmail(v)
                .map(this::toResponse)
                .map(List::of)
                .orElse(List.of());
    }

    @Transactional
    public void deactivateAccount(UUID userId) {
        update(userId, User::deactivate);
    }

    @Transactional
    public void deleteAccount(UUID userId) {
        User user = userRepository.getOrThrow(userId);

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
        binaryContentRepository.getOrThrow(req.profileId());
        update(userId, u -> {
            UUID old = u.getProfileId();
            u.changeProfileId(req.profileId());
            if (old != null && !old.equals(req.profileId())) {
                binaryContentRepository.softDeleteById(old);
            }
        });
    }

    @Transactional
    public void clearProfileImage(UUID userId) {
        User user = userRepository.getOrThrow(userId);
        if (user.getProfileId() == null) return;
        binaryContentRepository.softDeleteById(user.getProfileId());
        user.clearProfileId();
        userRepository.save(user);
    }

    @Transactional
    public void updateEmail(UUID userId, UserUpdateEmailRequest req) {
        String email = stripToLowerCase(req.email());
        User user = userRepository.getOrThrow(userId);

        if (user.getEmail().equals(email)) {
            throw new IllegalArgumentException("기존과 동일한 이메일입니다.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("중복된 이메일이 존재합니다.");
        }
        try {
            update(userId, u -> u.changeEmail(email));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("중복된 이메일이 존재합니다.");
        }
    }

    @Transactional
    public void updateUsername(UUID userId, UserUpdateUsernameRequest req) {
        String username = stripToLowerCase(req.username());
        User user = userRepository.getOrThrow(userId);

        if (user.getUsername().equals(username)) {
            throw new IllegalArgumentException("기존과 동일한 사용자명입니다.");
        }

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("중복된 사용자명이 존재합니다.");
        }

        try {
            update(userId, u -> u.changeUsername(username));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("중복된 사용자명이 존재합니다.");
        }
    }

    @Transactional
    public void updatePassword(UUID userId, UserUpdatePasswordRequest req) {
        String password = req.password().strip();
        User user = userRepository.getOrThrow(userId);
        if (passwordEncoder.matches(password, user.getPassword())) throw new DuplicateResourceException("동일한 비밀번호입니다.");
        update(userId, u -> u.changePassword(passwordEncoder.encode(password)));
    }

    @Transactional
    public void updatePhoneNumber(UUID userId, UserUpdatePhoneNumberRequest req) {
        String v = PhoneNumbers.normalizeToE164(req.phoneNumber());
        update(userId, u -> u.changePhoneNumber(v));
    }

    @Transactional
    public void clearPhoneNumber(UUID userId) {
        User user = userRepository.getOrThrow(userId);
        if (user.getPhoneNumber() == null) return;
        user.clearPhoneNumber();
        userRepository.save(user);
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
