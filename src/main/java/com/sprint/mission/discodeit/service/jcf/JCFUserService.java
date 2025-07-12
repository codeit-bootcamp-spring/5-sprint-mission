package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.ChatRoom;
import com.sprint.mission.discodeit.entity.Server;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.userEntity.Item;
import com.sprint.mission.discodeit.enums.userEntity.NitroPlan;
import com.sprint.mission.discodeit.enums.userEntity.Status;
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
    // @VisibleForTesting
    public void reset() {
        JCFUserService.getInstance().data.clear();
    }

    @Override
    public boolean registerUser(User user) {
        boolean exists = data.stream()
                .anyMatch(u -> u.getId().equals(user.getId()));
        if (exists) {
            System.out.println("중복된 id가 존재합니다.");
            return false;
        }
        data.add(user);
        return true;
    }

    @Override
    public User loginUser(String email, String password) {
        return data.stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User findById(UUID id) {
        return data.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return data.stream()
                .filter(u -> u.getEmail().equals(email))
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
                .filter(u -> u.getId().equals(user.getId()))
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
    public void updateBirthDate(User user, LocalDate birthDate) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setBirthDate(birthDate);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateIsSubscribedToNewsletter(User user, boolean isSubscribedToNewsletter) {
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
    public void updateStatus(User user, Status status) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setStatus(status);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateAvatarUrl(User user, String avatarUrl) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setAvatarUrl(avatarUrl);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateBio(User user, String bio) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setBio(bio);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateIsVerified(User user, boolean isVerified) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setVerified(isVerified);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateIsDeactivated(User user, boolean isDeactivated) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setDeactivated(isDeactivated);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateIsBanned(User user, boolean isBanned) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setBanned(isBanned);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateServers(User user, List<Server> servers) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setServers(servers);
                    u.setUpdatedAt(System.currentTimeMillis());
                });
    }

    @Override
    public void updateChatRooms(User user, List<ChatRoom> chatRooms) {
        data.stream()
                .filter(u -> u.getId().equals(user.getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.setChatRooms(chatRooms);
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
