package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Guild;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.user.Status;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserService extends BaseService<User> {
  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  User login(String email, String password);

  void logout(UUID userId);

  void deactivateAccount(UUID userId);

  void reactivateAccount(UUID userId);

  void deleteAccount(UUID userId);

  List<User> searchUsers(String keyword);

  void updateEmail(UUID userId, String email);

  void updateGlobalName(UUID userId, String globalName);

  void updateUsername(UUID userId, String username);

  void updatePassword(UUID userId, String password);

  void updateBirthDate(UUID userId, LocalDate birthDate);

  void updateSubscribedToNewsletter(UUID userId, boolean isSubscribedToNewsletter);

  void updatePhoneNumber(UUID userId, String phoneNumber);

  void updateStatus(UUID userId, Status status);

  void updateAvatar(UUID userId, String avatar);

  void updateBio(UUID userId, String bio);

  void updateVerified(UUID userId, boolean verified);

  void updateBanned(UUID userId, boolean banned);

  Set<User> getFriends(UUID userId);

  void addFriend(UUID userId, UUID friendId);

  void removeFriend(UUID userId, UUID friendId);

  Set<Guild> getGuilds(UUID userId);

  void addGuild(UUID userId, UUID guildId);

  void removeGuild(UUID userId, UUID guildId);

  void addChatRoom(UUID userId, UUID chatRoomId);

  void removeChatRoom(UUID userId, UUID chatRoomId);
}
