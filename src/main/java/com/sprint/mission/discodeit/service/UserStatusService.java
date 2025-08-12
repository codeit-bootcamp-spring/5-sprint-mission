package com.sprint.mission.discodeit.service;



import com.sprint.mission.discodeit.service.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.service.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.service.dto.userstatus.UserStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    // create
    UUID create(UserStatusCreateRequest request);

    // find
    UserStatusResponse find(UUID id);

    // findAll
    List<UserStatusResponse> findAll();

    // update (id 파라미터 + 수정 DTO)
    void update(UUID id, UserStatusUpdateRequest request);

    // updateByUserId
    void updateByUserId(UUID userId, UserStatusUpdateRequest request);

    // delete
    void delete(UUID id);
}

