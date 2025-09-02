package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;

  // 프로필 업로드/교체에 사용 (스토리지 연동)
  private final BinaryContentService binaryContentService;

  // DTO 변환 전담
  private final UserMapper userMapper;

  @Override
  @Transactional
  public User create(UserCreateRequest req, Optional<BinaryContentCreateRequest> optProfileReq) {
    String username = req.username();
    String email = req.email();

    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("User with email " + email + " already exists");
    }
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("User with username " + username + " already exists");
    }

    // 프로필 메타 + 스토리지 저장 (있다면)
    BinaryContent profile = optProfileReq.map(binaryContentService::create).orElse(null);

    User user = new User(username, email, req.password(), profile);
    User created = userRepository.save(user);

    // 상태 생성(참조 기반)
    userStatusRepository.save(new UserStatus(created, Instant.now()));

    return created;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto find(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    return userMapper.toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return userRepository.findAll().stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public User update(UUID userId, UserUpdateRequest req, Optional<BinaryContentCreateRequest> optProfileReq) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    String newUsername = req.newUsername();
    String newEmail = req.newEmail();

    if (newEmail != null && !newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
      throw new IllegalArgumentException("User with email " + newEmail + " already exists");
    }
    if (newUsername != null && !newUsername.equals(user.getUsername()) && userRepository.existsByUsername(newUsername)) {
      throw new IllegalArgumentException("User with username " + newUsername + " already exists");
    }

    // 프로필 교체(있다면 기존 메타 삭제 후 새로 저장)
    BinaryContent newProfile = optProfileReq.map(binaryContentService::create).orElse(null);
    if (newProfile != null && user.getProfile() != null) {
      // 메타만 삭제(스토리지 삭제는 요구사항 범위 밖이므로 생략)
      binaryContentService.delete(user.getProfile().getId());
    }

    user.update(newUsername, newEmail, req.newPassword(), newProfile);
    return userRepository.save(user);
  }

  @Override
  @Transactional
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    if (user.getProfile() != null) {
      binaryContentService.delete(user.getProfile().getId()); // 메타 삭제(스토리지 삭제는 선택 사항)
    }
    userStatusRepository.deleteById(userId);

    userRepository.deleteById(userId);
  }
}