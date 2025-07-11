package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Server;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.Item;
import com.sprint.mission.discodeit.enums.NitroPlan;
import com.sprint.mission.discodeit.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private static final JCFUserService instance = new JCFUserService();

    private final List<User> data;

    private JCFUserService() {
        data = new ArrayList<User>();
    }

    public static JCFUserService getInstance() {
        return instance;
    }

    @Override
    public void registerUser(User user) {
        boolean exists = data.stream()
                .anyMatch(u -> u.getId().equals(user.getId()));
        if (exists) {
            System.out.println("중복된 id가 존재합니다.");
            return;
        }
        data.add(user);
    }

    @Override
    public User findById(UUID id) {
        return data.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return data;
    }

    @Override
    public void updateEmail( User user, String email) {
        data.stream()
                .filter(u1 -> u1.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setEmail(email);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updatePassword(User user, String password) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setPassword(password);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updatePhoneNumber(User user, String phoneNumber) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setPhoneNumber(phoneNumber);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateUsername(User user, String username) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setUsername(username);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateNickname(User user, String nickname) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setNickname(nickname);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateBirthDay(User user, LocalDate birthDay) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setBirthDay(birthDay);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateSubscription(User user, boolean isSubscribedToNewsletter) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setSubscribedToNewsletter(isSubscribedToNewsletter);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateFriends(User user, List<User> friends) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setFriends(friends);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateNitroPlan(User user, NitroPlan nitroPlan) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setNitroPlan(nitroPlan);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateItems(User user, List<Item> items) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setItems(items);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateServers(User user, Server[] servers) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setServers(servers);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateDmRooms(User user, List<List<Message>> dmRooms) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setDmRooms(dmRooms);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void deleteById(UUID id) {
        data.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .ifPresent(data::remove);
    }
}
