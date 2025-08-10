package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserService {
    UserResponse create(UserCreateRequest request);

    UserResponse find(UUID userId);

    List<UserResponse> findAll();

    UserResponse update(UserUpdateRequest request);

    void delete(UUID id);

    Optional<UserResponse> findByUsername(String username);

    /**
     * 모든 사용자 데이터를 초기화합니다.
     * 테스트 환경에서 사용됩니다.
     */
    void clear();
}
