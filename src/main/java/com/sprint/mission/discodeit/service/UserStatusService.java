package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

//유저 상태
public interface UserStatusService {

    void create(UserStatusCreateRequest request);

    UserStatus findById(UUID id);

    List<UserStatus> findAll();

    void update(UserStatusUpdateRequest request);

    void updateByUserId(UserStatusUpdateByUserIdRequest request); // UserStatus ID를 모를 때 userId로 찾아서 수정하는 용도

    void delete(UUID id);

}
