package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.enums.user.Status;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public interface UserService {

    void register(String email,
                  String username,
                  String password,
                  LocalDate birthDate,
                  boolean subscribedToNewsletter,
                  String globalName);

    void login(String email, String password);

    void logout(UUID userId);

    void deactivateAccount(UUID userId);

    void reactivateAccount(UUID userId);

    void deleteAccount(UUID userId);

    void updateEmail(UUID userId, String email);

    void updateGlobalName(UUID userId, String globalName);

    void updateUsername(UUID userId, String username);

    void updatePassword(UUID userId, String password);

    void updateBirthDate(UUID userId, LocalDate birthDate);

    void updateSubscribedToNewsletter(UUID userId, boolean isSubscribedToNewsletter);

    void updatePhoneNumber(UUID userId, String phoneNumber);

    void updateStatus(UUID userId, Status status);

    void updateAvatar(UUID userId, String avatar);

    void updateBio(UUID userId, String bio);

    void updateVerified(UUID userId, boolean verified);

    void updateBanned(UUID userId, boolean banned);

    Set<UUID> getFriends(UUID userId);

    void addFriend(UUID userId, UUID friendId);

    void removeFriend(UUID userId, UUID friendId);

    Set<UUID> getGuilds(UUID userId);

    void joinGuild(UUID userId, UUID guildId);

    void leaveGuild(UUID userId, UUID guildId);

    void joinChatRoom(UUID userId, UUID chatRoomId);

    void leaveChatRoom(UUID userId, UUID chatRoomId);
}
