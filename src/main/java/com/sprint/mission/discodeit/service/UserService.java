package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    User create(UserDto.Create dto);

    List<User> findAll();

    User findById(UUID id);

    User findByEmail(String email);

    User update(UserDto.Update dto);

    boolean delete(UUID id);

}
