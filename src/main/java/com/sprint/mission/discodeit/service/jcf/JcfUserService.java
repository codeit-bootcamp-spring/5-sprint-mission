package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Guild;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.user.Status;
import com.sprint.mission.discodeit.service.FriendRequestService;
import com.sprint.mission.discodeit.service.GuildService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.validation.EmailValidator;
import com.sprint.mission.discodeit.validation.SaveUserValidator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class JcfUserService extends BaseJcfService<User> implements UserService {
  private static final JcfUserService instance = new JcfUserService();

  private FriendRequestService friendRequestService;
  private GuildService guildService;

  private JcfUserService() {}

  public static JcfUserService getInstance() {
    return instance;
  }

  public void setFriendRequestService(FriendRequestService friendRequestService) {
    this.friendRequestService = friendRequestService;
  }

  public void setGuildService(GuildService guildService) {
    this.guildService = guildService;
  }

  @Override
  public User findByEmail(String email) {
    return data.stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
  }

  @Override
  public User save(User user) {
    SaveUserValidator.isValid(user);

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
            .filter(u -> u.getEmail().equalsIgnoreCase(email) && u.checkPassword(password))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("이메일 또는 비밀번호가 일치하지 않습니다."));

    if (user.isBanned()) {
      throw new IllegalArgumentException("정지된 계정입니다.");
    }

    user.setDeactivated(false);
    user.setStatus(Status.ONLINE);
    user.touch();
    return user;
  }

  @Override
  public void logout(UUID userId) {
    update(userId, u -> u.setStatus(Status.OFFLINE));
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
  public void deleteAccount(UUID userId) {
    friendRequestService.clearFriendRequests(userId);

    User user = getOrThrow(userId);
    for (UUID friendId : new HashSet<>(user.getFriends())) {
      removeFriend(friendId, userId);
    }

    List<Guild> guildsToRemove = new ArrayList<>();
    for (UUID guildId : user.getGuilds()) {
      Guild guild = JcfGuildService.getInstance().getOrThrow(guildId);
      if (guild.getOwnerId().equals(userId)) {
        guildsToRemove.add(guild);
      }
    }
    for (Guild guild : guildsToRemove) {
      guildService.deleteById(guild.getId());
    }

    deleteById(userId);
  }

  @Override
  public List<User> searchUsers(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return List.of();
    }

    String lowerKeyword = keyword.toLowerCase();
    return data.stream()
        .filter(
            u ->
                u.getUsername().toLowerCase().contains(lowerKeyword)
                    || u.getEmail().toLowerCase().contains(lowerKeyword)
                    || u.getGlobalName().toLowerCase().contains(lowerKeyword))
        .toList();
  }

  @Override
  public void updateEmail(UUID userId, String email) {
    EmailValidator.isValid(email);

    User user = getOrThrow(userId);
    if (user.getEmail().equalsIgnoreCase(email)) {
      return;
    }

    if (findByEmail(email) != null) {
      throw new IllegalArgumentException("중복된 이메일이 존재합니다.");
    }

    update(userId, u -> u.setEmail(email));
  }

  @Override
  public void updateGlobalName(UUID userId, String globalName) {
    update(userId, u -> u.setGlobalName(globalName));
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
    if (getOrThrow(userId).checkPassword(password)) {
      throw new IllegalArgumentException("동일한 비밀번호입니다.");
    }
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
  public void updateAvatar(UUID userId, String avatar) {
    update(userId, u -> u.setAvatar(avatar));
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
  public void updateBanned(UUID userId, boolean banned) {
    update(userId, u -> u.setBanned(banned));
  }

  @Override
  public Set<User> getFriends(UUID userId) {
    User user = getOrThrow(userId);
    return user.getFriends().stream()
        .map(this::getOrThrow) // UUID → User
        .collect(Collectors.toSet());
  }

  @Override
  public void addFriend(UUID userId, UUID friendId) {
    if (userId.equals(friendId)) {
      throw new IllegalArgumentException("자기 자신에게는 친구 요청을 보낼 수 없습니다.");
    }
    getOrThrow(friendId);
    update(userId, u -> u.addFriend(friendId));
    update(friendId, u -> u.addFriend(userId));
  }

  @Override
  public void removeFriend(UUID userId, UUID friendId) {
    getOrThrow(friendId);
    update(userId, u -> u.removeFriend(friendId));
    update(friendId, u -> u.removeFriend(userId));
  }

  @Override
  public Set<Guild> getGuilds(UUID userId) {
    User user = getOrThrow(userId);
    return user.getGuilds().stream()
        .map(JcfGuildService.getInstance()::getOrThrow)
        .collect(Collectors.toSet());
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
  public void addChatRoom(UUID userId, UUID chatRoomId) {
    update(userId, u -> u.addChatRoom(chatRoomId));
  }

  @Override
  public void removeChatRoom(UUID userId, UUID chatRoomId) {
    update(userId, u -> u.removeChatRoom(chatRoomId));
  }
}
