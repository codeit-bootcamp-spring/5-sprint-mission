package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.user.Status;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.validation.EmailValidator;
import com.sprint.mission.discodeit.validation.PasswordValidator;
import com.sprint.mission.discodeit.validation.RegisterUserValidator;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JcfUserService extends JcfService<User> implements UserService {
  private static final JcfUserService instance = new JcfUserService();

  private JcfUserService() {}

  public static JcfUserService getInstance() {
    return instance;
  }

  @Override
  public User findByEmail(String email) {
    return data.stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
  }

  @Override
  public List<User> searchUsers(String keyword) {
    return List.of();
  }

  @Override
  public User searchUser(String keyword) {
    return null;
  }

  @Override
  public User registerUser(User user) {
    RegisterUserValidator.validate(user);

    if (findByEmail(user.getEmail()) != null) {
      throw new IllegalArgumentException("중복된 이메일이 존재합니다.");
    }

    data.add(user);
    return user;
  }

  @Override
  public User login(String email, String password) {
    User user =
        data.stream()
            .filter(u -> u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password))
            .findFirst()
            .orElse(null);

    if (user == null) {
      throw new NoSuchElementException("이메일 또는 패스워드가 일치하지 않습니다.");
    }

    if (user.isBanned()) {
      throw new IllegalArgumentException("정지된 계정입니다.");
    }

    user.setDeactivated(false);
    user.setStatus(Status.ONLINE);
    user.setUpdatedAt(System.currentTimeMillis());
    return user;
  }

  @Override
  public void logout(UUID userId) {
    User u = findById(userId);
    if (u != null) {
      u.setStatus(Status.OFFLINE);
      u.setUpdatedAt(System.currentTimeMillis());
    }
  }

  @Override
  public void updateEmail(UUID userId, String email) {
    EmailValidator.validate(email);

    User current = requireEntity(userId);
    if (current.getEmail().equalsIgnoreCase(email)) {
      return;
    }

    User duplicated = findByEmail(email);
    if (duplicated != null) {
      throw new IllegalArgumentException("중복된 이메일이 존재합니다.");
    }

    update(userId, u -> u.setEmail(email));
  }

  @Override
  public void updateNickname(UUID userId, String nickname) {
    update(userId, u -> u.setNickname(nickname));
  }

  @Override
  public void updateUsername(UUID userId, String username) {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("사용자명은 필수입니다.");
    }

    update(userId, u -> u.setUsername(username));
  }

  @Override
  public void updatePassword(UUID userId, String password) {
    PasswordValidator.validate(password);
    update(userId, u -> u.setPassword(password));
  }

  @Override
  public void updateBirthDate(UUID userId, LocalDate birthDate) {
    if (birthDate == null) {
      throw new IllegalArgumentException("생년월일은 필수입니다.");
    }
    update(userId, u -> u.setBirthDate(birthDate));
  }

  @Override
  public void updateSubscribedToNewsletter(UUID userId, boolean isSubscribedToNewsletter) {
    update(userId, u -> u.setSubscribedToNewsletter(isSubscribedToNewsletter));
  }

  @Override
  public void updatePhoneNumber(UUID userId, String phoneNumber) {
    update(userId, u -> u.setPhoneNumber(phoneNumber));
  }

  @Override
  public void updateStatus(UUID userId, Status status) {
    if (status == null) {
      throw new IllegalArgumentException("상태는 필수입니다.");
    }
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
  public void deactivateAccount(UUID userId) {
    update(userId, u -> u.setDeactivated(true));
  }

  @Override
  public void reactivateAccount(UUID userId) {
    update(userId, u -> u.setDeactivated(false));
  }

  @Override
  public void updateBanned(UUID userId, boolean isBanned) {
    update(userId, u -> u.setBanned(isBanned));
  }

  @Override
  public void addFriend(UUID userId, UUID friendId) {
    if (userId.equals(friendId)) {
      throw new IllegalArgumentException("뭐야 나잖아");
    }
    requireEntity(friendId);
    update(userId, u -> u.addFriend(friendId));
    update(friendId, u -> u.addFriend(userId));
  }

  @Override
  public void removeFriend(UUID userId, UUID friendId) {
    requireEntity(friendId);
    update(userId, u -> u.removeFriend(friendId));
    update(friendId, u -> u.removeFriend(userId));
  }

  @Override
  public void clearFriends(UUID userId) {
    update(userId, User::clearFriends);
    data.forEach(u -> u.removeFriend(userId));
  }

  @Override
  public void addGuild(UUID userId, UUID guildId) {
    update(userId, u -> u.addGuild(guildId));
  }

  @Override
  public void removeGuild(UUID userId, UUID guildId) {
    update(userId, u -> u.removeGuild(guildId));
  }

  @Override
  public void clearGuilds(UUID userId) {
    update(userId, User::clearGuilds);
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
}
