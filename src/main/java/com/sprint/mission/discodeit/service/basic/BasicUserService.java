package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UsernameAlreadyExistsException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final PasswordEncoder passwordEncoder;
    private final BasicAuthService authService;

    @Transactional
    @Override
    public UserDto create(UserCreateRequest userCreateRequest,
                          Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();

        log.info("유저 생성 요청: username={}, email={}", username, email);

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException();
        }

        BinaryContent profile = optionalProfileCreateRequest
                .map(req -> {
                    BinaryContent binaryContent = new BinaryContent(req.fileName(), (long) req.bytes().length, req.contentType());
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), req.bytes());
                    return binaryContent;
                })
                .orElse(null);

        String encodedPassword = passwordEncoder.encode(userCreateRequest.password());
        User user = User.createUser(username, email, encodedPassword, profile);
        userRepository.save(user);

        log.info("유저 생성 완료: userId={}", user.getId());
        return userMapper.toDto(user);
    }

    @Override
    public UserDto find(UUID userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public List<UserDto> findAll() {
        log.info("전체 유저 조회 요청");
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
                          Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        log.info("유저 수정 요청: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();

        if (newEmail != null && userRepository.existsByEmail(newEmail)) {
            throw new EmailAlreadyExistsException();
        }
        if (newUsername != null && userRepository.existsByUsername(newUsername)) {
            throw new UsernameAlreadyExistsException();
        }

        BinaryContent newProfile = optionalProfileCreateRequest
                .map(req -> {
                    BinaryContent binaryContent = new BinaryContent(req.fileName(), (long) req.bytes().length, req.contentType());
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), req.bytes());
                    return binaryContent;
                })
                .orElse(null);

        user.update(newUsername, newEmail, userUpdateRequest.newPassword(), newProfile);
        log.info("유저 수정 완료: userId={}", user.getId());
        return userMapper.toDto(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateRole(RoleUpdateRequest request) {
        log.info("유저 권한 변경 요청: userId={}", request.userId());

        User user = userRepository.findById(request.userId())
                .orElseThrow(UserNotFoundException::new);

        user.updateRole(request.newRole());
        log.info("권한 변경 완료: userId={}, newRole={}", user.getId(), user.getRole());

        authService.expireUserSessions(user.getUsername());

        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(userId);
        log.info("유저 삭제 완료: userId={}", userId);
    }
}