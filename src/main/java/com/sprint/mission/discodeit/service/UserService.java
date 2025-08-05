package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.CreateFile;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    /**
     * 신규 사용자를 등록합니다.
     *
     * @param userRequest  신규 사용자 정보
     * @param profileImage 프로필 이미지 (null = 기본 이미지 사용)
     * @return 등록된 사용자 정보, 실패 시 null
     */
    UserResponse register(
            CreateUserRequest userRequest,
            @Nullable CreateFile profileImage
    );

    /**
     * 사용자 ID로 조회합니다.
     *
     * @param id 사용자 UUID
     * @return 해당 사용자 정보 (없으면 Optional.empty)
     */
    Optional<UserResponse> getById(UUID id);

    /**
     * 이메일로 조회합니다.
     *
     * @param email 사용자 이메일
     * @return 해당 사용자 정보 (없으면 Optional.empty)
     */
    Optional<UserResponse> getByEmail(String email);

    /**
     * 사용자명으로 조회합니다.
     *
     * @param userName 사용자명
     * @return 해당 사용자 정보 (없으면 Optional.empty)
     */
    Optional<UserResponse> getByUserName(String userName);

    /**
     * 닉네임으로 사용자 리스트를 검색합니다.
     *
     * @param nickname 검색할 닉네임
     * @return 닉네임이 일치하는 사용자 목록 (부분 일치 가능)
     */
    List<UserResponse> searchByNickname(String nickname);

    /**
     * 모든 사용자 목록을 조회합니다.
     *
     * @return 전체 사용자 목록
     */
    List<UserResponse> getAll();

    /**
     * 사용자 정보를 수정합니다.
     *
     * @param userRequest  수정할 사용자 정보
     * @param profileImage 변경할 프로필 이미지 (null 변경 없음)
     * @return 수정된 사용자 정보
     */
    UserResponse update(
            UpdateUserRequest userRequest,
            @Nullable CreateFile profileImage
    );

    /**
     * 유저를 삭제합니다.
     *
     * @param userId 삭제할 사용자 ID
     * @return 삭제 성공 여부
     */
    boolean remove(UUID userId);
}
