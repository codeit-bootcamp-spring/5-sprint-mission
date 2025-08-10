package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.guild.Guild;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface UserService {

    // UserResponse register(UserRegisterCommand);
    //
    // UserResponse findById(UUID userId);
    //
    // List<UserResponse> findAll();
    //
    // UserResponse update(UserUpdateCommand cmd);
    User register(String email,
                  String username,
                  String password,
                  LocalDate birthDate,
                  boolean subscribedToNewsletter,
                  String globalName);

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

    void updateBio(UUID userId, String bio);

    void updateVerified(UUID userId, boolean verified);

    void updateBanned(UUID userId, boolean banned);

    List<User> getFriends(UUID userId);

    void addFriend(UUID userId, UUID friendId);

    void removeFriend(UUID userId, UUID friendId);

    List<Guild> getGuilds(UUID userId);

    void joinGuild(UUID userId, UUID guildId);

    void leaveGuild(UUID userId, UUID guildId);

    void joinChatRoom(UUID userId, UUID chatRoomId);

    void leaveChatRoom(UUID userId, UUID chatRoomId);

    void hardDeleteAccount(UUID userId);
}
