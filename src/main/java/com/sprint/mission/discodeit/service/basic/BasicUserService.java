package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UsernameAlreadyExistsException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

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

        log.info("유저 생성 요청: username={}, email={}", username, email);

        if (userRepository.existsByEmail(email)) {
            log.warn("중복 이메일로 회원가입 시도: {}", email);
            throw new EmailAlreadyExistsException();
        }
        if (userRepository.existsByUsername(username)) {
            log.warn("중복 username으로 회원가입 시도: {}", username);
            throw new UsernameAlreadyExistsException();
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
                .map(profileRequest -> {
                    log.debug("프로필 업로드 요청: fileName={}, size={}",
                            profileRequest.fileName(), profileRequest.bytes().length);
                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);
                    return binaryContent;
                })
                .orElse(null);

        User user = new User(username, email, userCreateRequest.password(), nullableProfile);
        Instant now = Instant.now();
        UserStatus userStatus = new UserStatus(user, now);

        userRepository.save(user);
        log.info("유저 생성 성공: userId={}", user.getId());

        return userMapper.toDto(user);
    }

    @Override
    public UserDto find(UUID userId) {
        log.info("유저 단건 조회 요청: userId={}", userId);
        return userRepository.findById(userId)
                .map(user -> {
                    log.debug("조회된 유저: {}", user);
                    return userMapper.toDto(user);
                })
                .orElseThrow(() -> {
                    log.error("유저 조회 실패: userId={}", userId);
                    return new UserNotFoundException();
                });
    }

    @Override
    public List<UserDto> findAll() {
        log.info("전체 유저 조회 요청");
        List<UserDto> users = userRepository.findAllWithProfileAndStatus()
                .stream()
                .map(userMapper::toDto)
                .toList();
        log.info("전체 유저 조회 완료: {}명", users.size());
        return users;
    }

    @Transactional
    @Override
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
                          Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        log.info("유저 수정 요청: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("유저 수정 실패 - 존재하지 않음: userId={}", userId);
                    throw new UserNotFoundException();
                });

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        if (userRepository.existsByEmail(newEmail)) {
            log.warn("중복 이메일로 수정 시도: {}", newEmail);
            throw new EmailAlreadyExistsException();
        }
        if (userRepository.existsByUsername(newUsername)) {
            log.warn("중복 username으로 수정 시도: {}", newUsername);
            throw new UsernameAlreadyExistsException();
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
                .map(profileRequest -> {
                    log.debug("프로필 수정 업로드: fileName={}, size={}",
                            profileRequest.fileName(), profileRequest.bytes().length);
                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);
                    return binaryContent;
                })
                .orElse(null);

        user.update(newUsername, newEmail, userUpdateRequest.newPassword(), nullableProfile);
        log.info("유저 수정 성공: userId={}", user.getId());

        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        log.info("유저 삭제 요청: userId={}", userId);
        if (!userRepository.existsById(userId)) {
            log.error("유저 삭제 실패 - 존재하지 않음: userId={}", userId);
            throw new UserNotFoundException();
        }

        userRepository.deleteById(userId);
        log.info("유저 삭제 성공: userId={}", userId);
    }
}