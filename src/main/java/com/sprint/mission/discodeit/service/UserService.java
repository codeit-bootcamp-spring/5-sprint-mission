package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.service.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.service.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.service.dto.user.UserView;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserView create(CreateUserRequest request);     // 프로필 이미지 선택 등록 + UserStatus 생성 + 유니크 검사
    UserView find(UUID userId);                     // 온라인 여부 포함, 비밀번호 제외
    List<UserView> findAll();                       // 온라인 여부 포함, 비밀번호 제외
    UserView update(UpdateUserRequest request);     // 선택적 프로필 교체/제거
    void delete(UUID userId);                       // User + UserStatus + BinaryContent(프로필) 연쇄 삭제
}
