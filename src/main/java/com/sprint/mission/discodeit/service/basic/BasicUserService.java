package com.sprint.mission.discodeit.service.basic;

<<<<<<< HEAD
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
=======
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
<<<<<<< HEAD
import java.time.Instant;
import java.util.ArrayList;
=======
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
import org.springframework.stereotype.Service;

@Service("basicUserService")
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponseDto create(UserCreateRequest request, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        if (userRepository.existsByName(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        UUID nullableProfileId = optionalProfileCreateRequest
                .map(profileRequest -> {
                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
                    return binaryContentRepository.save(binaryContent).getId();
                })
                .orElse(null);

        User user = new User(request.username(), request.email(), request.password(), nullableProfileId);
        User savedUser = userRepository.save(user);

        UserStatus status = new UserStatus(savedUser.getId(), Instant.now());
        userStatusRepository.save(status);

        return UserResponseDto.fromEntity(savedUser, status);
    }

    @Override
    public UserResponseDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        UserStatus status = userStatusRepository.findByUserId(userId).orElse(null);

        return UserResponseDto.fromEntity(user, status);
    }

    @Override
    public List<UserResponseDto> findAll() {
        List<User> users = userRepository.findAll();
        List<UserResponseDto> userResponseList = new ArrayList<>();

        for (User user : users) {
            UserStatus status = userStatusRepository.findByUserId(user.getId()).orElse(null);
            userResponseList.add(UserResponseDto.fromEntity(user, status));
        }
        return userResponseList;
    }

    @Override
    public UserResponseDto update(UUID userId, UserUpdateRequest request, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        if (userRepository.existsByEmail(request.email()) && !request.email().equals(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + request.email() + " already exists");
        }
        if (userRepository.existsByName(request.username()) && !request.username().equals(user.getUsername())) {
            throw new IllegalArgumentException("User with username " + request.username() + " already exists");
        }

        UUID nullableProfileId = optionalProfileCreateRequest
                .map(profileRequest -> {
                    Optional.ofNullable(user.getProfileId())
                            .ifPresent(binaryContentRepository::deleteById);

                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
                    return binaryContentRepository.save(binaryContent).getId();
                })
                .orElse(user.getProfileId());

        user.update(request.username(), request.email(), request.password(), nullableProfileId);
        User savedUser = userRepository.save(user);

        UserStatus status = userStatusRepository.findByUserId(userId).orElse(null);

        return UserResponseDto.fromEntity(savedUser, status);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }

        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found for userId: " + userId));

        userRepository.deleteById(userId);
        userStatusRepository.deleteById(status.getId());
    }
}
=======
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Transactional
  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.info("[USER][CREATE] username={}, email={}", userCreateRequest.username(), userCreateRequest.email());

    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    log.info("[USER][CREATE] username={}, email={}", username, email);
    if (userRepository.existsByEmail(email)) {
      log.warn("[USER][CREATE][FAIL] email exists: {}", email);
      throw new UserAlreadyExistsException(email);
    }
    if (userRepository.existsByUsername(username)) {
      log.warn("[USER][CREATE][FAIL] username exists: {}", username);
      throw new UserAlreadyExistsException(username);
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), bytes);
          return binaryContent;
        })
        .orElse(null);
    String password = userCreateRequest.password();

    User user = new User(username, email, password, nullableProfile);
    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(user, now);

    userRepository.save(user);
    UserDto dto = userMapper.toDto(user);
    log.debug("[USER][CREATE][DONE] id={}", dto.id());
    return dto;
  }

  @Override
  public UserDto find(UUID userId) {
    log.debug("[USER][FIND] id={}", userId);
    return userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
  }

  @Override
  public List<UserDto> findAll() {
    log.debug("[USER][FINDALL]");
    return userRepository.findAllWithProfileAndStatus()
        .stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.info("[USER][CREATE] username={}, email={}", userUpdateRequest.newUsername(), userUpdateRequest.newEmail());
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();
    if (userRepository.existsByEmail(newEmail)) {
      log.warn("[USER][CREATE][FAIL] email exists: {}", newEmail);
      throw new UserAlreadyExistsException(newEmail);
    }
    if (userRepository.existsByUsername(newUsername)) {
      log.warn("[USER][CREATE][FAIL] username exists: {}", newUsername);
      throw new UserAlreadyExistsException(newUsername);
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {

          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), bytes);
          return binaryContent;
        })
        .orElse(null);

    String newPassword = userUpdateRequest.newPassword();
    log.info("[USER][UPDATE] id={}", userId);
    user.update(newUsername, newEmail, newPassword, nullableProfile);
    UserDto dto = userMapper.toDto(user);
    log.debug("[USER][UPDATE][DONE] id={}", userId);
    return dto;
  }

  @Transactional
  @Override
  public void delete(UUID userId) {
    log.warn("[USER][DELETE] id={}", userId);
    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException(userId);
      }
    userRepository.deleteById(userId);
    log.debug("[USER][DELETE][DONE] id={}", userId);
    }
}
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
