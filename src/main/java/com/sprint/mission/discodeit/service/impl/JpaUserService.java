package com.sprint.mission.discodeit.service.impl;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.guild.Guild;
import com.sprint.mission.discodeit.domain.enums.user.Status;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Profile("prod")
@Transactional
public class JpaUserService implements UserService {
    @Override
    public User register(
            String email,
            String username,
            String password,
            LocalDate birthDate,
            boolean subscribedToNewsletter,
            String globalName) {
        return null;
    }

    @Override
    public void login(String email, String password) {

    }

    @Override
    public void logout(UUID userId) {

    }

    @Override
    public void deactivateAccount(UUID userId) {

    }

    @Override
    public void reactivateAccount(UUID userId) {

    }

    @Override
    public void deleteAccount(UUID userId) {

    }

    @Override
    public void updateEmail(UUID userId, String email) {

    }

    @Override
    public void updateGlobalName(UUID userId, String globalName) {

    }

    @Override
    public void updateUsername(UUID userId, String username) {

    }

    @Override
    public void updatePassword(UUID userId, String password) {

    }

    @Override
    public void updateBirthDate(UUID userId, LocalDate birthDate) {

    }

    @Override
    public void updateSubscribedToNewsletter(UUID userId, boolean isSubscribedToNewsletter) {

    }

    @Override
    public void updatePhoneNumber(UUID userId, String phoneNumber) {

    }

    @Override
    public void updateStatus(UUID userId, Status status) {

    }

    @Override
    public void updateAvatar(UUID userId, String avatar) {

    }

    @Override
    public void updateBio(UUID userId, String bio) {

    }

    @Override
    public void updateVerified(UUID userId, boolean verified) {

    }

    @Override
    public void updateBanned(UUID userId, boolean banned) {

    }

    @Override
    public List<User> getFriends(UUID userId) {
        return List.of();
    }

    @Override
    public void addFriend(UUID userId, UUID friendId) {

    }

    @Override
    public void removeFriend(UUID userId, UUID friendId) {

    }

    @Override
    public List<Guild> getGuilds(UUID userId) {
        return List.of();
    }

    @Override
    public void joinGuild(UUID userId, UUID guildId) {

    }

    @Override
    public void leaveGuild(UUID userId, UUID guildId) {

    }

    @Override
    public void joinChatRoom(UUID userId, UUID chatRoomId) {

    }

    @Override
    public void leaveChatRoom(UUID userId, UUID chatRoomId) {

    }

    @Override
    public void hardDeleteAccount(UUID userId) {

    }
}
