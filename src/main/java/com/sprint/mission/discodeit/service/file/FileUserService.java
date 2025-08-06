package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.user.Status;
import com.sprint.mission.discodeit.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileUserService extends BaseFileService<User> implements UserService {
    public FileUserService() {
        super(User.class);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return findAll().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public User login(String email, String password) {
        User user = findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email) && u.checkPassword(password))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("이메일 또는 비밀번호가 일치하지 않습니다."));

        if (user.isBanned()) {
            throw new IllegalArgumentException("정지된 계정입니다.");
        }

        update(user.getId(), u -> {
            u.setDeactivated(false);
            u.setStatus(Status.ONLINE);
        });

        return getOrThrow(user.getId());
    }

    @Override
    public void logout(UUID userId) {
        update(userId, u -> u.setStatus(Status.OFFLINE));
    }

    @Override
    public void deactivateAccount(UUID userId) {
        update(userId, u -> u.setDeactivated(true));
    }

    @Override
    public void reactivateAccount(UUID userId) {
        update(userId, u -> u.setDeactivated(false));
    }

    @Override
    public void deleteAccount(UUID userId) {
        deleteById(userId);
        // 의존성
    }

    @Override
    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("검색어를 입력해주세요.");
        }

        String lowerKeyword = keyword.toLowerCase();

        return findAll().stream()
                .filter(User::isActive)
                .filter(u ->
                        u.getGlobalName().toLowerCase().contains(lowerKeyword)
                                || u.getUsername().toLowerCase().contains(lowerKeyword)
                                || u.getEmail().toLowerCase().contains(lowerKeyword))
                .toList();
    }

    @Override
    public void updateEmail(UUID userId, String email) {
        if (getOrThrow(userId).getEmail().equalsIgnoreCase(email)) {
            throw new IllegalArgumentException("기존과 동일한 이메일입니다.");
        }

        if (findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("중복된 이메일이 존재합니다.");
        }

        update(userId, u -> u.setEmail(email));
    }

    @Override
    public void updateGlobalName(UUID userId, String globalName) {
        update(userId, u -> u.setGlobalName(globalName));
    }

    @Override
    public void updateUsername(UUID userId, String username) {
        if (findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("중복된 사용자명이 존재합니다.");
        }
        update(userId, u -> u.setUsername(username));
    }

    @Override
    public void updatePassword(UUID userId, String password) {
        if (getOrThrow(userId).checkPassword(password)) {
            throw new IllegalArgumentException("동일한 비밀번호입니다.");
        }
        update(userId, u -> u.setPassword(password));
    }

    @Override
    public void updateBirthDate(UUID userId, LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("생년월일은 필수입니다.");
        }
        update(userId, u -> u.setBirthDate(birthDate));
    }

    @Override
    public void updateSubscribedToNewsletter(UUID userId, boolean sub) {
        update(userId, u -> u.setSubscribedToNewsletter(sub));
    }

    @Override
    public void updatePhoneNumber(UUID userId, String phone) {
        update(userId, u -> u.setPhoneNumber(phone));
    }

    @Override
    public void updateStatus(UUID userId, Status status) {
        if (status == null) {
            throw new IllegalArgumentException("상태는 필수입니다.");
        }
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
        update(userId, u -> u.setVerified(verified));
    }

    @Override
    public void updateBanned(UUID userId, boolean banned) {
        update(userId, u -> u.setBanned(banned));
    }

    @Override
    public Set<User> getFriends(UUID userId) {
        return getOrThrow(userId).getFriends().stream()
                .map(this::getOrThrow)
                .collect(Collectors.toSet());
    }

    @Override
    public void addFriend(UUID userId, UUID friendId) {
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("자기 자신에게는 친구 요청을 보낼 수 없습니다.");
        }
        getOrThrow(userId);
        getOrThrow(friendId);
        update(userId, u -> u.addFriend(friendId));
        update(friendId, u -> u.addFriend(userId));
    }

    @Override
    public void removeFriend(UUID userId, UUID friendId) {
        getOrThrow(userId);
        getOrThrow(friendId);
        update(userId, u -> u.removeFriend(friendId));
        update(friendId, u -> u.removeFriend(userId));
    }

    @Override
    public Set<UUID> getGuilds(UUID userId) {
        return getOrThrow(userId).getGuilds();
    }

    @Override
    public void addGuild(UUID userId, UUID guildId) {
        update(userId, u -> u.addGuild(guildId));
    }

    @Override
    public void removeGuild(UUID userId, UUID guildId) {
        update(userId, u -> u.removeGuild(guildId));
    }

    @Override
    public void addChatRoom(UUID userId, UUID chatRoomId) {
        update(userId, u -> u.addChatRoom(chatRoomId));
    }

    @Override
    public void removeChatRoom(UUID userId, UUID chatRoomId) {
        update(userId, u -> u.removeChatRoom(chatRoomId));
    }
}
