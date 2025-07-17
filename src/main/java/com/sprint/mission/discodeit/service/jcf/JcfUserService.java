package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.userentity.Status;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.validation.RegisterUserValidator;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Consumer;

public class JcfUserService extends JcfService<User> implements UserService {
  private static final JcfUserService instance = new JcfUserService();

  private JcfUserService() {}

  public static JcfUserService getInstance() {
    return instance;
  }

  @Override
  public boolean idEquals(User user, UUID id) {
    return user.getId().equals(id);
  }

  @Override
  public boolean emailEquals(User user, String email) {
    return user.getEmail().equals(email);
  }

  @Override
  public User findByEmail(String email) {
    return data.stream().filter(u -> u.getEmail().equals(email)).findFirst().orElse(null);
  }

  @Override
  public void update(UUID userId, Consumer<User> updater) {
    User u = findById(userId);
    if (u != null) {
      updater.accept(u);
      u.setUpdatedAt(System.currentTimeMillis());
    }
  }

  @Override
  public User registerUser(User user) {
    try {
      RegisterUserValidator.validate(user);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return null;
    }
    boolean emailDuplicated = data.stream().anyMatch(u -> emailEquals(u, user.getEmail()));
    if (emailDuplicated) {
      System.out.println("중복된 이메일이 존재합니다.");
      return null;
    }
    data.add(user);
    return user;
  }

  @Override
  public User loginUser(String email, String password) {
    return data.stream()
        .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
        .findFirst()
        .orElse(null);
  }

  @Override
  public void updateEmail(UUID userId, String email) {
    update(userId, u -> u.setEmail(email));
  }

  @Override
  public void updatePassword(UUID userId, String password) {
    update(userId, u -> u.setPassword(password));
  }

  @Override
  public void updatePhoneNumber(UUID userId, String phoneNumber) {
    update(userId, u -> u.setPhoneNumber(phoneNumber));
  }

  @Override
  public void updateUsername(UUID userId, String username) {
    update(userId, u -> u.setUsername(username));
  }

  @Override
  public void updateNickname(UUID userId, String nickname) {
    update(userId, u -> u.setNickname(nickname));
  }

  @Override
  public void updateBirthDate(UUID userId, LocalDate birthDate) {
    update(userId, u -> u.setBirthDate(birthDate));
  }

  @Override
  public void updateSubscribedToNewsletter(UUID userId, boolean isSubscribedToNewsletter) {
    update(userId, u -> u.setSubscribedToNewsletter(isSubscribedToNewsletter));
  }

  @Override
  public void addFriend(UUID userId, UUID friendId) {
    update(userId, u -> u.addFriend(friendId));
  }

  @Override
  public void removeFriend(UUID userId, UUID friendId) {
    update(userId, u -> u.removeFriend(friendId));
  }

  @Override
  public void clearFriends(UUID userId) {
    update(userId, User::clearFriends);
  }

  @Override
  public void addServer(UUID userId, UUID serverId) {
    update(userId, u -> u.addServer(serverId));
  }

  @Override
  public void removeServer(UUID userId, UUID serverId) {
    update(userId, u -> u.removeServer(serverId));
  }

  @Override
  public void clearServers(UUID userId) {
    update(userId, User::clearServers);
  }

  @Override
  public void addChatRoom(UUID userId, UUID chatRoomId) {
    update(userId, u -> u.addChatRoom(chatRoomId));
  }

  @Override
  public void removeChatRoom(UUID userId, UUID chatRoomId) {
    update(userId, u -> u.removeChatRoom(chatRoomId));
  }

  @Override
  public void clearChatRooms(UUID userId) {
    update(userId, User::clearChatRooms);
  }

  @Override
  public void updateStatus(UUID userId, Status status) {
    update(userId, u -> u.setStatus(status));
  }

  @Override
  public void updateAvatarUrl(UUID userId, String avatarUrl) {
    update(userId, u -> u.setAvatarUrl(avatarUrl));
  }

  @Override
  public void updateBio(UUID userId, String bio) {
    update(userId, u -> u.setBio(bio));
  }

  @Override
  public void updateVerified(UUID userId, boolean isVerified) {
    update(userId, u -> u.setVerified(isVerified));
  }

  @Override
  public void updateDeactivated(UUID userId, boolean isDeactivated) {
    update(userId, u -> u.setDeactivated(isDeactivated));
  }

  @Override
  public void updateBanned(UUID userId, boolean isBanned) {
    update(userId, u -> u.setBanned(isBanned));
  }
}
