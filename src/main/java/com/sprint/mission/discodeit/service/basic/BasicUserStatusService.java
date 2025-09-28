package com.sprint.mission.discodeit.service.basic;

<<<<<<< HEAD
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatus;
=======
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.exception.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
<<<<<<< HEAD
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponseDto create(UserStatusCreateRequest request) {
        UUID userId = UUID.randomUUID();

        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found with id " + userId);
        }
        if (userStatusRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("UserStatus with id " + userId + " already exists");
        }
        UserStatus userStatus = new UserStatus(userId, request.lastActiveAt());

        UserStatus saved = userStatusRepository.save(userStatus);

        return UserStatusResponseDto.fromEntity(saved);
    }

    @Override
    public UserStatusResponseDto find(UUID id) {
        UserStatus status = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found with id " + id));
        return UserStatusResponseDto.fromEntity(status);
    }

    @Override
    public List<UserStatusResponseDto> findAll() {
        List<UserStatus> allStatus = userStatusRepository.findAll();
        return allStatus.stream()
                .map(UserStatusResponseDto::fromEntity)
                .toList();
    }

    @Override
    public UserStatusResponseDto update(UUID userStatusId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();
        UserStatus status = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found with id " + userStatusId));

        status.update(newLastActiveAt);
        UserStatus updated = userStatusRepository.save(status);

        return UserStatusResponseDto.fromEntity(updated);
    }

    @Override
    public UserStatusResponseDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newLastActiveAt();

        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // UserStatus 없으면 새로 생성
                    return new UserStatus(userId, newLastActiveAt);
                });

        // 업데이트
        status.update(newLastActiveAt);

        UserStatus updated = userStatusRepository.save(status);

        return UserStatusResponseDto.fromEntity(updated);
    }


    @Override
    public void delete(UUID id) {
        if (!userStatusRepository.existsById(id)) {
            throw new NoSuchElementException("UserStatus not found with id " + id);
        }
        userStatusRepository.deleteById(id);
    }
=======
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Transactional
  @Override
  public UserStatusDto create(UserStatusCreateRequest request) {
    log.info("[USER_STATUS][CREATE] userId={}", request.userId());
    UUID userId = request.userId();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
    Optional.ofNullable(user.getStatus())
        .ifPresent(status -> {
          throw new UserStatusAlreadyExistsException(userId);
        });

    Instant lastActiveAt = request.lastActiveAt();
    UserStatus userStatus = new UserStatus(user, lastActiveAt);
    userStatusRepository.save(userStatus);
    UserStatusDto dto = userStatusMapper.toDto(userStatus);
    log.debug("[USER_STATUS][CREATE][DONE] id={}", dto.id());
    return dto;
  }

  @Override
  public UserStatusDto find(UUID userStatusId) {
    log.debug("[USER_STATUS][FIND] id={}", userStatusId);
    UserStatusDto dto = userStatusRepository.findById(userStatusId)
        .map(userStatusMapper::toDto)
        .orElseThrow(() -> new UserStatusNotFoundException(userStatusId));
    log.debug("[USER_STATUS][FIND][DONE] id={}", dto.id());
    return dto;
  }

  @Override
  public List<UserStatusDto> findAll() {
    log.debug("[USER_STATUS][FIND_ALL]");
    List<UserStatusDto> userStatusDtos = userStatusRepository.findAll().stream()
        .map(userStatusMapper::toDto)
        .toList();
    log.debug("[USER_STATUS][FIND_ALL][DONE] userStatusDtos={}", userStatusDtos);
    return userStatusDtos;
  }

  @Transactional
  @Override
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    log.info("[USER_STATUS][UPDATE] id={}, newLastActiveAt={}", userStatusId, request.newLastActiveAt());
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new UserStatusNotFoundException(userStatusId));
    userStatus.update(newLastActiveAt);
    UserStatusDto dto = userStatusMapper.toDto(userStatus);
    log.debug("[USER_STATUS][UPDATE][DONE] id={}", dto.id());
    return dto;
  }

  @Transactional
  @Override
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    log.info("[USER_STATUS][UPDATE_BY_USER_ID] userId={}, newLastActiveAt={}", userId, request.newLastActiveAt());
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(
            () -> new UserStatusNotFoundException(userId));
    userStatus.update(newLastActiveAt);
    UserStatusDto userStatusDto = userStatusMapper.toDto(userStatus);
    log.debug("[USER_STATUS][UPDATE_BY_USER_ID][DONE] id={}", userStatusDto.id());
    return userStatusDto;
  }

  @Transactional
  @Override
  public void delete(UUID userStatusId) {
    log.warn("[USER_STATUS][DELETE] id={}", userStatusId);
    if (!userStatusRepository.existsById(userStatusId)) {
      throw new UserStatusNotFoundException(userStatusId);
    }
    userStatusRepository.deleteById(userStatusId);
    log.debug("[USER_STATUS][DELETE][DONE] id={}", userStatusId);
  }
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
}
