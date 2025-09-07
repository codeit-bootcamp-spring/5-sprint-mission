package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * 리팩토링 포인트
 * 1) 엔티티 정합성: UserStatus는 user(FK)를 보유하므로 user를 로딩해 연관을 맺어 저장한다.
 * 2) 생성/수정 트랜잭션: @Transactional 적용, 조회는 readOnly=true로 성능/일관성 확보.
 * 3) 중복검증 메시지 명확화: "해당 userId의 상태가 이미 존재"로 표현.
 * 4) update 로직: 엔티티에 update(...)가 없으므로 newLastActiveAt이 있을 때 touch(...)로 갱신.
 * 5) findAll: 스트림 불필요, 바로 반환.
 */
@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository; // UserStatus JPA 레포지토리
    private final UserRepository userRepository;             // User JPA 레포지토리

    @Override
    @Transactional
    public UserStatus create(UserStatusCreateRequest request) {
        // 1) 요청 파라미터 추출
        UUID userId = request.userId();                      // 상태를 가질 사용자 ID
        Instant lastActiveAt = request.lastActiveAt();       // 마지막 활동 시각(없으면 now로 대체 가능)

        // 2) 사용자 존재 여부 검증
        User user = userRepository.findById(userId)          // 사용자 엔티티 로딩(연관 설정 필요)
                .orElseThrow(() ->
                        new NoSuchElementException("User with id " + userId + " does not exist"));

        // 3) 해당 사용자에 대한 상태 중복 존재 여부 검증
        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("UserStatus for user " + userId + " already exists");
        }

        // 4) 상태 엔티티 생성 및 연관관계 설정
        UserStatus status = new UserStatus(                  // 엔티티 생성(시각 주입)
                lastActiveAt != null ? lastActiveAt : Instant.now()
        );
        // - 양방향 연관 관계 설정(둘 중 하나만 해도 되지만, 편의 메서드가 있다면 attach 사용 권장)
        //   여기서는 직접 세터를 호출하여 FK(user_id)를 설정한다.
        status.setUser(user);

        // 5) 저장
        return userStatusRepository.save(status);
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatus find(UUID userStatusId) {
        // PK로 단건 조회
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() ->
                        new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserStatus> findAll() {
        // 전체 목록 반환(스트림 변환 불필요)
        return userStatusRepository.findAll();
    }

    @Override
    @Transactional
    public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request) {
        // 1) 대상 로딩
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() ->
                        new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));

        // 2) 변경 사항 적용: newLastActiveAt이 있을 때만 반영
        Instant newLastActiveAt = request.newLastActiveAt();
        if (newLastActiveAt != null) {
            // 엔티티에 update가 없다면 touch로 갱신
            userStatus.touch(newLastActiveAt);
        }

        // 3) 저장 및 반환
        return userStatusRepository.save(userStatus);
    }

    @Override
    @Transactional
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        // 1) userId 기준 상태 로딩
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("UserStatus with userId " + userId + " not found"));

        // 2) 변경 사항 적용
        Instant newLastActiveAt = request.newLastActiveAt();
        if (newLastActiveAt != null) {
            userStatus.touch(newLastActiveAt);
        }

        // 3) 저장 및 반환
        return userStatusRepository.save(userStatus);
    }

    @Override
    @Transactional
    public void delete(UUID userStatusId) {
        // 존재 여부 검증(없으면 예외)
        if (!userStatusRepository.existsById(userStatusId)) {
            throw new NoSuchElementException("UserStatus with id " + userStatusId + " not found");
        }
        // 삭제
        userStatusRepository.deleteById(userStatusId);
    }
}
