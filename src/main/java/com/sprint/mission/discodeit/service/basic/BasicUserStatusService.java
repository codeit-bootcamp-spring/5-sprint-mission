package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.userStatus.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.AlreadyExistsUserStatusException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserStatusResponse create(UserStatusCreateRequest request) {
        log.info("[Service] 유저 상태 생성 시도");
        log.debug("[Service] 유저 상태 생성 요청 데이터: {}", request);
        if (!userRepository.existsById(request.getUserId())) {
            throw UserNotFoundException.withId(request.getUserId());
        }

        if (userStatusRepository.findByUserId(request.getUserId()).isPresent()) {
            throw AlreadyExistsUserStatusException.withUserId(request.getUserId());
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> UserNotFoundException.withId(request.getUserId()));

        UserStatus userStatus = new UserStatus(user);
        userStatusRepository.save(userStatus);

        log.info("[Service] 유저 상태 생성 완료: {}", userStatus);
        return UserStatusResponse.success(userStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatusResponse getById(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> UserNotFoundException.withId(id));

        return UserStatusResponse.success(userStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserStatusResponse> getAll() {
        List<UserStatus> userStatuses = userStatusRepository.findAll();

        return userStatuses.stream()
                .map(UserStatusResponse::success)
                .toList();
    }


    @Override
    @Transactional
    public UserStatusResponse update(UUID userStatusId, UserStatusUpdateRequest request) {
        log.info("[Service] 유저 상태 업데이트 시도");
        log.debug("[Service] 유저 상태 업데이트 요청 데이터: {}", request);
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> UserStatusNotFoundException.withUserStatusId(userStatusId));

        userStatus.updateLastActiveAt(request.getNewLastActiveAt());
        userStatusRepository.save(userStatus);

        log.info("[Service] 유저 상태 업데이트 완료: {}", userStatus);
        return UserStatusResponse.success(userStatus);
    }

    @Override
    @Transactional
    public UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        log.info("[Service] 유저 ID로 유저 상태 업데이트 시도");
        log.debug("[Service] 유저 ID로 유저 상태 업데이트 요청 데이터: {}", request);
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> UserStatusNotFoundException.withUserId(userId));

        userStatus.updateLastActiveAt(request.getNewLastActiveAt());
        userStatusRepository.save(userStatus);

        log.info("[Service] 유저 ID로 유저 상태 업데이트 완료: {}", userStatus);
        return UserStatusResponse.success(userStatus);
    }

    @Override
    @Transactional
    public UserStatusResponse delete(UUID id) {
        log.info("[Service] 유저 상태 삭제 시도: ID={}", id);
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> UserStatusNotFoundException.withUserStatusId(id));

        userStatusRepository.deleteById(id);

        log.info("[Service] 유저 상태 삭제 완료: {}", userStatus);
        return UserStatusResponse.success(userStatus);
    }

    @Override
    @Transactional
    public boolean isOnline(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> UserStatusNotFoundException.withUserId(userId));

        Instant lastActiveAt = userStatus.getLastActiveAt();

        if (lastActiveAt == null) {
            return false;
        }

        return lastActiveAt.isAfter(Instant.now().minusSeconds(300));
    }
}