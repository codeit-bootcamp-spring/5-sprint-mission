package com.codeit.mission.discodeit.service.basic;

import com.codeit.mission.discodeit.dto.data.UserDto;
import com.codeit.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.codeit.mission.discodeit.dto.request.UserCreateRequest;
import com.codeit.mission.discodeit.dto.request.UserUpdateRequest;
import com.codeit.mission.discodeit.entity.BinaryContent;
import com.codeit.mission.discodeit.entity.User;
import com.codeit.mission.discodeit.entity.UserStatus;
import com.codeit.mission.discodeit.mapper.UserMapper;
import com.codeit.mission.discodeit.repository.BinaryContentRepository;
import com.codeit.mission.discodeit.repository.UserRepository;
import com.codeit.mission.discodeit.repository.UserStatusRepository;
import com.codeit.mission.discodeit.service.UserService;
import com.codeit.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
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
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();

        log.info("사용자 생성 요청 - username: {}, email: {}", username, email);

        if (userRepository.existsByEmail(email)) {
            log.warn("사용자 생성 실패 - 중복된 이메일: {}", email);
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
        if (userRepository.existsByUsername(username)) {
            log.warn("사용자 생성 실패 - 중복된 사용자명: {}", username);
            throw new IllegalArgumentException(
                    "User with username " + username + " already exists");
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
                .map(profileRequest -> {
                    log.debug("프로필 이미지 업로드 처리 - fileName: {}, size: {} bytes",
                            profileRequest.fileName(), profileRequest.bytes().length);
                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                            contentType);
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);

                    log.debug("프로필 이미지 저장 완료 - binaryContentId: {}", binaryContent.getId());
                    return binaryContent;
                })
                .orElse(null);
        String password = userCreateRequest.password();

        User user = new User(username, email, password, nullableProfile);
        Instant now = Instant.now();
        UserStatus userStatus = new UserStatus(user, now);

        userRepository.save(user);
        log.info("사용자 생성 완료 - userId: {}, username: {}", user.getId(), username);
        log.debug("생성된 사용자 상세 정보 - userId: {}, email: {}, hasProfile: {}",
                user.getId(), email, nullableProfile != null);

        return userMapper.toDto(user);
    }

    @Override
    public UserDto find(UUID userId) {
        log.debug("사용자 조회 요청 - userId: {}", userId);

        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(
                        () -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    @Override
    public List<UserDto> findAll() {
        log.debug("전체 사용자 조회 요청");

        return userRepository.findAllWithProfileAndStatus()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
            Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new NoSuchElementException("User with id " + userId + " not found"));

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();

        log.info("사용자 수정 요청 - userId: {}, newUsername: {}, newEmail: {}",
                userId, newUsername, newEmail);

        if (userRepository.existsByEmail(newEmail)) {
            log.warn("사용자 수정 실패 - 중복된 이메일: {}", newEmail);
            throw new IllegalArgumentException("User with email " + newEmail + " already exists");
        }
        if (userRepository.existsByUsername(newUsername)) {
            log.warn("사용자 수정 실패 - 중복된 사용자명: {}", newUsername);
            throw new IllegalArgumentException(
                    "User with username " + newUsername + " already exists");
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
                .map(profileRequest -> {
                    log.debug("새 프로필 이미지 업로드 처리 - fileName: {}, size: {} bytes",
                            profileRequest.fileName(), profileRequest.bytes().length);

                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                            contentType);
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);

                    log.debug("새 프로필 이미지 저장 완료 - binaryContentId: {}", binaryContent.getId());
                    return binaryContent;
                })
                .orElse(null);

        String newPassword = userUpdateRequest.newPassword();
        user.update(newUsername, newEmail, newPassword, nullableProfile);

        log.debug("수정된 사용자 상세 정보 - userId: {}, hasNewProfile: {}",
                userId, nullableProfile != null);
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        log.info("사용자 삭제 요청 - userId: {}", userId);

        if (userRepository.existsById(userId)) {
            log.warn("사용자 삭제 실패 - 존재하지 않는 userId: {}", userId);
            throw new NoSuchElementException("User with id " + userId + " not found");
        }

        userRepository.deleteById(userId);
        log.info("사용자 삭제 완료 - userId: {}", userId);
    }
}
