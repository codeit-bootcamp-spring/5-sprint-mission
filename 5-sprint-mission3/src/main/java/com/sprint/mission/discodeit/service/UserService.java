package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserRequest;
import com.sprint.mission.discodeit.dto.UserFindResponse;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserRequest request);
    UserFindResponse find(UUID userId);
    List<UserFindResponse> findAll();
    User update(UserRequest request);
    void delete(UUID userId);
}
