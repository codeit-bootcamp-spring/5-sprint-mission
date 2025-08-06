package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UpdateDto;
import com.sprint.mission.discodeit.dto.user.JoinDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(JoinDto dto);
    User find(UUID userId);
    List<User> findAll();
    User update(UpdateDto dto);
    void delete(UUID userId);
}
