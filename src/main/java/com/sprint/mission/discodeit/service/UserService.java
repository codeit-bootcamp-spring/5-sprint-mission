package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(User user);

    User create(String name, String email, String password);

    List<User> getAll();

    User get(UUID id);

    User update(UUID id, String name, UUID profileId);

    // TODO mission 3 인터페이스 정리 예정 : create, find, findall, update, delete

    UserDto.DetailResponse create(UserDto.CreateRequest request);

    UserDto.DetailResponse update(UserDto.UpdateRequest request);

    UserDto.DetailResponse findById(UUID id);

    List<UserDto.DetailResponse> findAll();

    void delete(UUID id);

    void deleteAll();


}
