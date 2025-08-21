package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.AddUserRequest;
import com.sprint.mission.discodeit.dto.response.GetUserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserService implements com.sprint.mission.discodeit.service.UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public User addUser(AddUserRequest addUserRequest) {
    Optional<User> byUserName = userRepository.findByUserName(addUserRequest.userName());
    Optional<User> byEmail = userRepository.findByEmail(addUserRequest.email());
    if (byEmail.isPresent() || byUserName.isPresent()) {
      throw new IllegalArgumentException("중복된 User Name 혹은 User Email");
    }

    User user = new User(addUserRequest.userName(), addUserRequest.email(),
        addUserRequest.password(), addUserRequest.phoneNumber(), addUserRequest.profileId());
    UserStatus userStatus = new UserStatus(user.getId());
    Optional<User> addedUser = userRepository.save(user);
    userStatusRepository.save(userStatus).orElseThrow();
    return addedUser.orElseThrow();
  }


  @Override
  public GetUserResponse getUserById(UUID userId) {
    User user = userRepository.findById(userId).orElseThrow();
    UserStatus userStatus = userStatusRepository.findByUserId(userId).orElseThrow();

    return new GetUserResponse(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUserName(),
        user.getEmail(),
        user.getProfileId(),
        userStatus.isOnline());
  }

  @Override
  public List<GetUserResponse> getAllUser() {
    List<User> users = userRepository.findAll();
    List<GetUserResponse> returnList = new ArrayList<>();
    for (User user : users) {
      UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElseThrow();
      GetUserResponse getUserResponse = new GetUserResponse(
          user.getId(),
          user.getCreatedAt(),
          user.getUpdatedAt(),
          user.getUserName(),
          user.getEmail(),
          user.getProfileId(),
          userStatus.isOnline());
      returnList.add(getUserResponse);
    }
    return returnList;
  }

  @Override
  public User updateUser(UUID userId, AddUserRequest addUserRequest) {
    User user = userRepository.findById(userId).orElseThrow();

    user.updateUserName(addUserRequest.userName());
    user.updateEmail(addUserRequest.email());
    user.updatePassword(addUserRequest.password());
    user.updatePhoneNumber(addUserRequest.phoneNumber());
    user.updateProfileId(addUserRequest.profileId());

    return userRepository.save(user).orElseThrow();
  }

  @Override
  public void deleteUser(UUID userId) {
    User user = userRepository.findById(userId).orElseThrow();
    userStatusRepository.deleteByUserId(userId);
    binaryContentRepository.deleteById(user.getProfileId());
    userRepository.delete(userId);
  }

  @Override
  public void deleteAllUser() {
    userStatusRepository.deleteAll();
    binaryContentRepository.deleteAll();
    userRepository.deleteAll();
  }
}
