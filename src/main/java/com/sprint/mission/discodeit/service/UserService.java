package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.enums.userEntity.Item;
import com.sprint.mission.discodeit.enums.userEntity.NitroPlan;
import com.sprint.mission.discodeit.enums.userEntity.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface UserService extends Service {
    boolean registerUser(User user);

    User loginUser(String email, String password);

    User findById(UUID id);

    User findByEmail(String email);

    List<User> findAll();

    void deleteById(UUID id);

    void updateEmail(User user, String email);

    void updatePassword(User user, String password);

    void updatePhoneNumber(User user, String phoneNumber);

    void updateUsername(User user, String username);

    void updateNickname(User user, String nickname);

    void updateBirthDate(User user, LocalDate birthDate);

    void updateIsSubscribedToNewsletter(User user, boolean isSubscribedToNewsletter);

    void updateFriends(User user, List<User> friends);

    void updateServers(User user, List<Server> servers);

    void updateChatRooms(User user, List<ChatRoom> chatRooms);

    void updateNitroPlan(User user, NitroPlan nitroPlan);

    void updateItems(User user, List<Item> items);

    void updateStatus(User user, Status status);

    void updateAvatarUrl(User user, String avatarUrl);

    void updateBio(User user, String bio);

    void updateIsVerified(User user, boolean isVerified);

    void updateIsDeactivated(User user, boolean isDeactivated);

    void updateIsBanned(User user, boolean isBanned);
}
