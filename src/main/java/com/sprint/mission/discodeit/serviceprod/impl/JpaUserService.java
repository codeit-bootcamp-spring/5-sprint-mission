package com.sprint.mission.discodeit.serviceprod.impl;

import com.sprint.mission.discodeit.domain.entityprod.ProdGuild;
import com.sprint.mission.discodeit.domain.entityprod.ProdUser;
import com.sprint.mission.discodeit.domain.enums.user.Status;
import com.sprint.mission.discodeit.repositoryprod.ProdFriendRequestRepository;
import com.sprint.mission.discodeit.repositoryprod.ProdGuildRepository;
import com.sprint.mission.discodeit.repositoryprod.ProdUserRepository;
import com.sprint.mission.discodeit.serviceprod.ProdUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Profile("prod")
public class JpaUserService implements ProdUserService {
    private final ProdUserRepository userRepository;
    private final ProdFriendRequestRepository friendRequestRepository;
    private final ProdGuildRepository guildRepository;

    @Override
    public ProdUser register(
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
    public void updateBio(UUID userId, String bio) {

    }

    @Override
    public void updateVerified(UUID userId, boolean verified) {

    }

    @Override
    public void updateBanned(UUID userId, boolean banned) {

    }

    @Override
    public List<ProdUser> getFriends(UUID userId) {
        return List.of();
    }

    @Override
    public void addFriend(UUID userId, UUID friendId) {

    }

    @Override
    public void removeFriend(UUID userId, UUID friendId) {

    }

    @Override
    public List<ProdGuild> getGuilds(UUID userId) {
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
