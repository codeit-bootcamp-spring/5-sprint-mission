package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("userStatusService")
@RequiredArgsConstructor
@Validated
public class BasicUserStatusService implements UserStatusService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;

  @Override
  public UserStatus create(@Valid UserStatusCreateRequest userStatusCreateRequest) {
    userRepository.findById(userStatusCreateRequest.userId())
        .orElseThrow(() -> new NoSuchElementException("create : 유저를 찾을 수 없습니다."));
    UserStatus userStatus = userStatusRepository.findAll().stream()
        .filter(s -> s.getUserId().equals(userStatusCreateRequest.userId()))
        .findFirst()
        .orElse(null);
    if (userStatus != null) {
      throw new IllegalArgumentException("create : UserStatus가 이미 존재합니다.");
    }
    userStatus = new UserStatus(userStatusCreateRequest.userId());
    return userStatusRepository.save(userStatus);
  }

  @Override
  public UserStatus findById(UUID id) {
    return userStatusRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("findById : UserStatus를 찾을 수 없습니다."));
  }

  @Override
  public UserStatus findByUserId(UUID userid) {
    return userStatusRepository.findByUserId(userid)
        .orElseThrow(() -> new NoSuchElementException("findByUserId : UserStatus를 찾을 수 없습니다."));
  }

  @Override
  public List<UserStatus> findAll() {
    return userStatusRepository.findAll();
  }

  @Override
  public UserStatus update(@Valid UserStatusUpdateRequest userStatusUpdateRequest) {
    UserStatus userStatus = userStatusRepository.findById(userStatusUpdateRequest.id())
        .orElseThrow(() -> new NoSuchElementException("update : UserStatus를 찾을 수 없습니다."));
    userStatus.update(userStatusUpdateRequest.loginStatus());

    return userStatusRepository.save(userStatus);
  }

  @Override
  public UserStatus updateByUserId(@Valid UserStatusUpdateRequest userStatusUpdateRequest) {
    UserStatus userStatus = userStatusRepository.findByUserId(userStatusUpdateRequest.id())
        .orElseThrow(() -> new NoSuchElementException("updateByUserId : UserStatus를 찾을 수 없습니다."));
    userStatus.update(userStatusUpdateRequest.loginStatus());

    return userStatusRepository.save(userStatus);
  }

  @Override
  public void delete(UUID id) {
    UserStatus userStatus = userStatusRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("delete : UserStatus를 찾을 수 없습니다."));
    userStatusRepository.deleteById(userStatus.getId());
  }
}
