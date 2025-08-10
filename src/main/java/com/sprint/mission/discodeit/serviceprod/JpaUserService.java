package com.sprint.mission.discodeit.serviceprod;

import com.sprint.mission.discodeit.domain.entityprod.ProdGuild;
import com.sprint.mission.discodeit.domain.entityprod.ProdUser;
import com.sprint.mission.discodeit.domain.enums.user.Status;
import com.sprint.mission.discodeit.repositoryprod.ProdFriendRequestRepository;
import com.sprint.mission.discodeit.repositoryprod.ProdGuildRepository;
import com.sprint.mission.discodeit.repositoryprod.ProdUserRepository;
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
public class JpaUserService {
    private final ProdUserRepository userRepository;
    private final ProdFriendRequestRepository friendRequestRepository;
    private final ProdGuildRepository guildRepository;

    public ProdUser register(
            String email,
            String username,
            String password,
            LocalDate birthDate,
            boolean subscribedToNewsletter,
            String globalName) {
        return null;
    }

    public void login(String email, String password) {

    }

    public void logout(UUID userId) {

    }

    public void deactivateAccount(UUID userId) {

    }

    public void reactivateAccount(UUID userId) {

    }

    public void deleteAccount(UUID userId) {

    }

    public void updateEmail(UUID userId, String email) {

    }

    public void updateGlobalName(UUID userId, String globalName) {

    }

    public void updateUsername(UUID userId, String username) {

    }

    public void updatePassword(UUID userId, String password) {

    }

    public void updateBirthDate(UUID userId, LocalDate birthDate) {

    }

    public void updateSubscribedToNewsletter(UUID userId, boolean isSubscribedToNewsletter) {

    }

    public void updatePhoneNumber(UUID userId, String phoneNumber) {

    }

    public void updateStatus(UUID userId, Status status) {

    }

    public void updateBio(UUID userId, String bio) {

    }

    public void updateVerified(UUID userId, boolean verified) {

    }

    public void updateBanned(UUID userId, boolean banned) {

    }

    public List<ProdUser> getFriends(UUID userId) {
        return List.of();
    }

    public void addFriend(UUID userId, UUID friendId) {

    }

    public void removeFriend(UUID userId, UUID friendId) {

    }

    public List<ProdGuild> getGuilds(UUID userId) {
        return List.of();
    }

    public void joinGuild(UUID userId, UUID guildId) {

    }

    public void leaveGuild(UUID userId, UUID guildId) {

    }

    public void joinChatRoom(UUID userId, UUID chatRoomId) {

    }

    public void leaveChatRoom(UUID userId, UUID chatRoomId) {

    }

    public void hardDeleteAccount(UUID userId) {

    }
}
