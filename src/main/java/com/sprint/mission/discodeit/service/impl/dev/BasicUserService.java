package com.sprint.mission.discodeit.service.impl.dev;

import com.sprint.mission.discodeit.domain.deventity.DevUser;
import com.sprint.mission.discodeit.domain.deventity.guild.DevGuild;
import com.sprint.mission.discodeit.domain.enums.user.Status;
import com.sprint.mission.discodeit.repository.devrepository.DevFriendRequestRepository;
import com.sprint.mission.discodeit.repository.devrepository.DevGuildRepository;
import com.sprint.mission.discodeit.repository.devrepository.DevUserRepository;
import com.sprint.mission.discodeit.service.dev.DevUserService;
import com.sprint.mission.discodeit.util.Validators;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Profile({"test", "dev"})
public class BasicUserService implements DevUserService {

    private final DevUserRepository userRepository;
    private final DevFriendRequestRepository friendRequestRepository;
    private final DevGuildRepository guildRepository;

    public BasicUserService(
            DevUserRepository userRepository,
            DevFriendRequestRepository friendRequestRepository,
            DevGuildRepository guildRepository
    ) {
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.guildRepository = guildRepository;
    }

    protected void update(UUID id, Consumer<DevUser> updater) {
        DevUser entity = userRepository.getOrThrow(id);
        updater.accept(entity);
        userRepository.save(entity);
    }

    @Override
    public DevUser register(String email,
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
        DevUser user = new DevUser(e, u, p, birthDate, subscribedToNewsletter, globalName);
        return userRepository.save(user);
    }

    @Override
    public void login(String email, String password) {
        String e = Validators.validateEmail(email);
        String p = Validators.validatePassword(password);

        DevUser user = userRepository.findByEmail(e)
                .filter(u -> u.checkPassword(p))
                .orElseThrow(() -> new NoSuchElementException("이메일 또는 비밀번호가 일치하지 않습니다."));

        if (user.isBanned()) throw new IllegalArgumentException("정지된 계정입니다.");

        update(user.getId(), u -> {
            u.activate();
            u.setStatus(Status.ONLINE);
        });
    }

    @Override
    public void logout(UUID userId) {
        update(userId, u -> u.setStatus(Status.OFFLINE));
    }

    @Override
    public void deactivateAccount(UUID userId) {
        update(userId, DevUser::deactivate);
    }

    @Override
    public void reactivateAccount(UUID userId) {
        update(userId, DevUser::activate);
    }

    @Override
    public void deleteAccount(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("userId는 null일 수 없습니다.");

        Set<UUID> frIds = new HashSet<>();
        friendRequestRepository.clear(userId);

        DevUser user = userRepository.getOrThrow(userId);
        for (UUID friendId : new HashSet<>(user.getFriends())) {
            update(friendId, f -> f.removeFriend(userId));
            update(userId, u -> u.removeFriend(friendId));
        }

        for (UUID guildId : new HashSet<>(user.getGuilds())) {
            DevGuild guild = guildRepository.getOrThrow(guildId);
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
        DevUser user = userRepository.getOrThrow(userId);
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
    public void updateStatus(UUID userId, Status status) {
        if (status == null) throw new IllegalArgumentException("상태는 필수입니다.");
        update(userId, u -> u.setStatus(status));
    }

    @Override
    public void updateAvatar(UUID userId, String avatar) {
        update(userId, u -> u.setAvatar(avatar));
    }

    @Override
    public void updateBio(UUID userId, String bio) {
        update(userId, u -> u.setBio(bio));
    }

    @Override
    public void updateVerified(UUID userId, boolean verified) {
        update(userId, DevUser::verify);
    }

    @Override
    public void updateBanned(UUID userId, boolean banned) {
        if (banned) update(userId, DevUser::ban);
        else update(userId, DevUser::unban);
    }

    @Override
    public List<DevUser> getFriends(UUID userId) {
        Set<UUID> ids = userRepository.getOrThrow(userId).getFriends();
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
    public List<DevGuild> getGuilds(UUID userId) {
        Set<UUID> ids = userRepository.getOrThrow(userId).getGuilds();
        return guildRepository.findAllByIds(ids);
    }

    @Override
    public void joinGuild(UUID userId, UUID guildId) {
        update(userId, u -> u.joinGuild(guildId));
    }

    @Override
    public void leaveGuild(UUID userId, UUID guildId) {
        update(userId, u -> u.leaveGuild(guildId));
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
