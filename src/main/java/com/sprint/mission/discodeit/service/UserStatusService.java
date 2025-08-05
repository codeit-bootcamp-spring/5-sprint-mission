package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {


    UserStatusDto.DetailResponse create(UserStatusDto.CreateRequest request);

    UserStatusDto.DetailResponse find(UUID id);

    List<UserStatusDto.DetailResponse> findAll();

    UserStatusDto.DetailResponse update(UUID id);

    UserStatusDto.DetailResponse updateByUserId(UUID userId);

    void delete(UUID id);

    void deleteAll();
}
