package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    // User 객체 생성 (사용자 생성)
    User create(UserCreateRequest userCreateRequest, // 사용자 정보를 Dto로 받음
                Optional<BinaryContentCreateRequest> profileCreateRequest); // 프로필 이미지 같은 바이너리 부가 데이터(없을 수도 있음)를 Dto로 받음

    // 사용자 식별자를 통해 사용자를 조회함, 응답용으로 UserDto에 담겨서 출력
    UserDto find(UUID userId);

    // 사용자 전체 조회, 사용자 목록을 UserDto 리스트로 반환
    List<UserDto> findAll();

    // 사용자 수정 (+ 선택적으로 프로필 바이너리 교체),
    User update(UUID userId, UserUpdateRequest userUpdateRequest, // 식별자, 변경할 엔터티들, 사용자 정보를 Dto로 받음
                Optional<BinaryContentCreateRequest> profileCreateRequest); // 프로필 이미지 같은 바이너리 부가 데이터(없을 수도 있음)를 Dto로 받음

    // 식별자로 삭제
    void delete(UUID userId);
}
