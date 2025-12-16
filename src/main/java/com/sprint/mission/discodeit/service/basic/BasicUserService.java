package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.UserUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.InvalidUserCredentialsException;
import com.sprint.mission.discodeit.exception.user.InvalidUserParameterException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentService binaryContentService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final JwtRegistry<UUID> jwtRegistry;

  @Override
  @Transactional
  public UserDTO createUser(UserCreateRequest userCreateRequest, MultipartFile profile) {

    // 1. username, email 호환성 확인
    if (userRepository.findByUsername(userCreateRequest.username()).isPresent()) {
      log.warn("이미 같은 아이디가 존재합니다. username={}", userCreateRequest.username());
      throw DuplicateUserException.withUsername(userCreateRequest.username());
    }

    if (userRepository.findByEmail(userCreateRequest.email()).isPresent()) {
      log.warn("이미 같은 이메일이 존재합니다. email={}", userCreateRequest.email());
      throw DuplicateUserException.withEmail(userCreateRequest.email());
    }

    // 2. 선택적으로 프로필 이미지를 같이 등록함. 있으면 등록 없으면 등록 안함.
    BinaryContent binaryContent = binaryContentService.createBinaryContent(profile);

    // 3. user, userStatus 같이 생성.
    User result = User.builder()
        .username(userCreateRequest.username())
        .email(userCreateRequest.email())
        .password(passwordEncoder.encode(userCreateRequest.password())) // Password Bcrypt Encoder
        .role(UserRole.USER)
        .profile(binaryContent)
        .build();

    // log 추가
    log.info("생성할 유저의 아이디 ={}", result.getUsername());
    try {
      User save = userRepository.save(result);

      log.debug("계정 생성 완료 ={}", save.getId());
      UserDTO userDTO = userMapper.toDto(save);
      applicationEventPublisher.publishEvent(new UserUpdatedEvent("users.created", userDTO));
      return userDTO;
    } catch (Exception e) {
      log.error("계정 생성에 실패하였습니다. ={}", result.getUsername(), e);
      throw InvalidUserParameterException.withMessage(e.getMessage());
    }
  }

  @Override
  @Transactional(readOnly = true)
  public UserDTO findByUserId(UUID userId) {
    // 1. 호환성 체크	user, userStatus Id(toDto가 함) 체크
    User save = userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::new);
    return userMapper.toDto(save);
  }

  @Cacheable(value = "users", key = "'all'")
  @Override
  @Transactional(readOnly = true)
  public List<UserDTO> findAll() {
    List<User> find = userRepository.findAll();
    return userMapper.toDto(find);
  }

  @PreAuthorize("principal.userDTO.id == #userId or hasRole('ADMIN')")
  @Override
  @Transactional
  public UserDTO updateUser(UUID userId, UserUpdateRequest userUpdateRequest,
      MultipartFile profile) {
    // 1. User 호환성 체크
    User user = userRepository.findById(userId).orElseThrow(() -> {
      log.warn("존재하지 않는 회원 업데이트 시도 userId={}", userId);
      return new UserNotFoundException();
    });

    BinaryContent binaryContent = binaryContentService.createBinaryContent(profile);

    // 3. Builder를 사용해서 profile 반영
    User updatedUser = user.toBuilder()
        .profile(binaryContent != null ? binaryContent : user.getProfile())
        .username(userUpdateRequest.newUsername() != null ? userUpdateRequest.newUsername()
            : user.getUsername())
        .email(
            userUpdateRequest.newEmail() != null ? userUpdateRequest.newEmail() : user.getEmail())
        .password(userUpdateRequest.newPassword() != null ? passwordEncoder.encode(
            userUpdateRequest.newPassword())
            : user.getPassword())
        .build();

    log.info("업데이트할 유저의 아이디 ={}", updatedUser.getUsername());
    try {
      User save = userRepository.save(updatedUser);

      jwtRegistry.invalidateJwtInformationByUserId(save.getId());
      log.debug("업데이트된 유저의 아이디 ={}", save.getId());
      UserDTO userDTO = userMapper.toDto(save);
      applicationEventPublisher.publishEvent(new UserUpdatedEvent("users.updated", userDTO));
      return userDTO;
    } catch (Exception e) {
      log.error("계정 업데이트에 실패하였습니다. ={}", user.getUsername(), e);
      throw InvalidUserParameterException.withMessage(e.getMessage());
    }
  }


  @PreAuthorize("hasRole('ADMIN')")
  @Override
  @Transactional
  public UserDTO updateRoleUser(UserRoleUpdateRequest userRoleUpdateRequest) {
    User user = userRepository.findById(userRoleUpdateRequest.userId()).orElseThrow(() -> {
      log.warn("존재하지 않는 회원 권한 업데이트 시도 userId={}", userRoleUpdateRequest.userId());
      return new UserNotFoundException();
    });

    UserRole oldRole = user.getRole();    // 예전 유저권한을 저장하기 위한 객체
    user.updateRole(userRoleUpdateRequest.newRole());

    try {
      User save = userRepository.save(user);
      applicationEventPublisher.publishEvent(
          new RoleUpdatedEvent(save.getId(), oldRole, save.getRole()));
      jwtRegistry.invalidateJwtInformationByUserId(save.getId());
      log.debug("업데이트된 유저의 아이디 = {}, 권한 ={}", save.getId(), save.getRole());
      UserDTO userDTO = userMapper.toDto(save);
      applicationEventPublisher.publishEvent(new UserUpdatedEvent("users.updated", userDTO));
      return userDTO;
    } catch (Exception e) {
      log.error("계정 업데이트에 실패하였습니다. ={}", user.getUsername(), e);
      throw InvalidUserParameterException.withMessage(e.getMessage());
    }
  }

  @Caching(evict = {
      @CacheEvict(value = "notifications", key = "#userId")
  })
  @PreAuthorize("principal.userDTO.id == #userId or hasRole('ADMIN')")
  @Override
  @Transactional
  public void deleteUser(UUID userId) {
    log.info("계정을 삭제합니다. ={}", userId);
    User user = userRepository.findById(userId).orElseThrow(() -> {
      log.warn("존재하지 않는 회원 삭제 시도 userId={}", userId);
      return new NoSuchElementException("존재하지 않는 회원입니다.");
    });

    try {
      // 1. user 안에 있는 profile 삭제
      if (user.getProfile() != null) {
        binaryContentService.delete(user.getProfile().getId());
      }

      // 2. 관련 도메인 삭제: User
      userRepository.deleteById(userId);
      jwtRegistry.invalidateJwtInformationByUserId(userId);
      UserDTO userDTO = userMapper.toDto(user);
      applicationEventPublisher.publishEvent(new UserUpdatedEvent("users.deleted", userDTO));
      log.debug("계정 삭제 완료 username={}", user.getId());
    } catch (Exception e) {
      log.error("계정 삭제 실패 ", e);
      throw InvalidUserCredentialsException.withMessage(e.getMessage());
    }
  }
}
