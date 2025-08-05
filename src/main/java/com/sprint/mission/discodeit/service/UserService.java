package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserDto.DetailResponse create(UserDto.CreateRequest request);

    UserDto.DetailResponse update(UserDto.UpdateRequest request);

    UserDto.DetailResponse findById(UUID id);

    List<UserDto.DetailResponse> findAll();

    void delete(UUID id);

    void deleteAll();


}
