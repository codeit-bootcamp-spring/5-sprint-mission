package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.userentity.Status;
import java.time.LocalDate;
import java.util.UUID;

public interface UserService extends Service<User> {
  User findByEmail(String email);

  boolean registerUser(User user);

  User loginUser(String email, String password);

  void updateEmail(UUID userId, String email);

  void updateNickname(UUID userId, String nickname);

  void updateUsername(UUID userId, String username);

  void updatePassword(UUID userId, String password);

  void updateBirthDate(UUID userId, LocalDate birthDate);

  void updateSubscribedToNewsletter(UUID userId, boolean isSubscribedToNewsletter);

  void updatePhoneNumber(UUID userId, String phoneNumber);

  void addFriend(UUID userId, UUID friendId);

  void removeFriend(UUID userId, UUID friendId);

  void clearFriends(UUID userId);

  void addServer(UUID userId, UUID serverId);

  void removeServer(UUID userId, UUID serverId);

  void clearServers(UUID userId);

  void addChatRoom(UUID userId, UUID chatRoomId);

  void removeChatRoom(UUID userId, UUID chatRoomId);

  void clearChatRooms(UUID userId);

  void updateStatus(UUID userId, Status status);

  void updateAvatarUrl(UUID userId, String avatarUrl);

  void updateBio(UUID userId, String bio);

  void updateVerified(UUID userId, boolean isVerified);

  void updateDeactivated(UUID userId, boolean isDeactivated);

  void updateBanned(UUID userId, boolean isBanned);
}
