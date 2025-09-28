package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor // Lombok: final 필드들을 생성자 주입으로 초기화하는 생성자 자동 생성
@Service // 스프링 서비스 컴포넌트로 등록
public class BasicUserStatusService implements UserStatusService { // 사용자 상태(UserStatus) 도메인 로직을 담당하는 서비스 구현체

    private final UserStatusRepository userStatusRepository; // UserStatus 엔티티 저장/조회 리포지토리
    private final UserRepository userRepository; // User 엔티티 조회 리포지토리
    private final UserStatusMapper userStatusMapper; // UserStatus ↔ UserStatusDto 변환 매퍼

    @Transactional // 생성 작업을 트랜잭션으로 처리
    @Override // 인터페이스 메서드 구현
    public UserStatusDto create(UserStatusCreateRequest request) { // 사용자 상태 생성
        UUID userId = request.userId(); // 요청으로부터 사용자 ID 추출

        User user = userRepository.findById(userId) // 사용자 존재 여부 확인 및 조회
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found")); // 없으면 예외

        Optional.ofNullable(user.getStatus()) // 이미 상태가 존재하는지 확인
                .ifPresent(status -> { // 존재한다면
                    throw new IllegalArgumentException("UserStatus with id " + userId + " already exists"); // 중복 생성 방지
                });

        Instant lastActiveAt = request.lastActiveAt(); // 마지막 활동 시각 추출
        UserStatus userStatus = new UserStatus(user, lastActiveAt); // 사용자 상태 엔티티 생성
        userStatusRepository.save(userStatus); // 상태 저장

        return userStatusMapper.toDto(userStatus); // 저장된 엔티티를 DTO로 변환하여 반환
    }

    @Override // 인터페이스 메서드 구현
    public UserStatusDto find(UUID userStatusId) { // 사용자 상태 단건 조회
        return userStatusRepository.findById(userStatusId) // ID로 조회
                .map(userStatusMapper::toDto) // 존재 시 DTO로 매핑
                .orElseThrow(
                        () -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found")); // 없으면 예외
    }

    @Override // 인터페이스 메서드 구현
    public List<UserStatusDto> findAll() { // 사용자 상태 전체 조회
        return userStatusRepository.findAll().stream() // 전체 목록 조회
                .map(userStatusMapper::toDto) // DTO로 변환
                .toList(); // 리스트로 수집하여 반환
    }

    @Transactional // 수정 작업을 트랜잭션으로 처리
    @Override // 인터페이스 메서드 구현
    public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) { // 사용자 상태 수정(식별자 기준)
        Instant newLastActiveAt = request.newLastActiveAt(); // 새 마지막 활동 시각 추출

        UserStatus userStatus = userStatusRepository.findById(userStatusId) // 대상 상태 조회
                .orElseThrow(
                        () -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found")); // 없으면 예외
        userStatus.update(newLastActiveAt); // 엔티티 상태 변경(더티 체킹으로 flush)

        return userStatusMapper.toDto(userStatus); // 수정된 엔티티를 DTO로 변환하여 반환
    }

    @Transactional // 수정 작업을 트랜잭션으로 처리
    @Override // 인터페이스 메서드 구현
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) { // 사용자 ID 기준 상태 수정
        Instant newLastActiveAt = request.newLastActiveAt(); // 새 마지막 활동 시각 추출

        UserStatus userStatus = userStatusRepository.findByUserId(userId) // 사용자 ID로 상태 조회
                .orElseThrow(
                        () -> new NoSuchElementException("UserStatus with userId " + userId + " not found")); // 없으면 예외
        userStatus.update(newLastActiveAt); // 엔티티 상태 변경

        return userStatusMapper.toDto(userStatus); // DTO로 변환하여 반환
    }

    @Transactional // 삭제 작업을 트랜잭션으로 처리
    @Override // 인터페이스 메서드 구현
    public void delete(UUID userStatusId) { // 사용자 상태 삭제
        if (!userStatusRepository.existsById(userStatusId)) { // 존재 여부 선검증
            throw new NoSuchElementException("UserStatus with id " + userStatusId + " not found"); // 없으면 예외
        }
        userStatusRepository.deleteById(userStatusId); // 삭제 실행
    }
}

