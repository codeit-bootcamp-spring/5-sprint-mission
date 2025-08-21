package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.AddUserRequest;
import com.sprint.mission.discodeit.dto.response.GetUserResponse;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {

  User addUser(AddUserRequest addUserRequest);

  GetUserResponse getUserById(UUID userId);

  List<GetUserResponse> getAllUser();

  User updateUser(UUID userId, AddUserRequest addUserRequest);

  void deleteUser(UUID userId);

  void deleteAllUser();
}

