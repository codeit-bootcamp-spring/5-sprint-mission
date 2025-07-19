package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.user.Status;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface UserService extends BaseService<User> {
  User findByEmail(String email);

  User register(User user);

  User login(String email, String password);

  void logout(UUID userId);

  void deactivateAccount(UUID userId);

  void reactivateAccount(UUID userId);

  void deleteAccount(UUID userId);

  List<User> searchUsers(String keyword);

  void updateEmail(UUID userId, String email);

  void updateNickname(UUID userId, String nickname);

  void updateUsername(UUID userId, String username);

  void updatePassword(UUID userId, String password);

  void updateBirthDate(UUID userId, LocalDate birthDate);

  void updateSubscribedToNewsletter(UUID userId, boolean isSubscribedToNewsletter);

  void updatePhoneNumber(UUID userId, String phoneNumber);

  void updateStatus(UUID userId, Status status);

  void updateAvatarUrl(UUID userId, String avatarUrl);

  void updateBio(UUID userId, String bio);

  void updateVerified(UUID userId, boolean isVerified);

  void updateBanned(UUID userId, boolean isBanned);

  void addFriend(UUID userId, UUID friendId);

  void removeFriend(UUID userId, UUID friendId);

  void addGuild(UUID userId, UUID guildId);

  void removeGuild(UUID userId, UUID guildId);

  void addChatRoom(UUID userId, UUID chatRoomId);

  void removeChatRoom(UUID userId, UUID chatRoomId);
}
