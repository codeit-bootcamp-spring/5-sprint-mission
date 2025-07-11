package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Server;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.Item;
import com.sprint.mission.discodeit.enums.NitroPlan;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface UserService {
    boolean registerUser(User user);

    User loginUser(String email, String password);

    User findById(UUID id);

    List<User> findAll();

    void updateEmail(User user, String email);

    void updatePassword(User user, String password);

    void updatePhoneNumber(User user, String phoneNumber);

    void updateUsername(User user, String username);

    void updateNickname(User user, String nickname);

    void updateBirthDate(User user, LocalDate birthDate);

    void updateSubscription(User user, boolean isSubscribedToNewsletter);

    void updateFriends(User user, List<User> friends);

    void updateNitroPlan(User user, NitroPlan nitroPlan);

    void updateItems(User user, List<Item> items);

    void updateServers(User user, Server[] servers);

    void updateDmRooms(User user, List<List<Message>> dmRooms);

    void deleteById(UUID id);
}
