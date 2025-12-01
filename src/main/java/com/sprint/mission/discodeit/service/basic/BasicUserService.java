package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.binaryContent.UserProfileImageRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserDeleteResponse;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final PasswordEncoder passwordEncoder;
    private final JwtRegistry jwtRegistry;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public UserResponse create(UserCreateRequest request) {
        log.info("[Service] 유저 생성 시도");
        log.debug("[Service] 유저 생성 요청 데이터: {}", request);
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("[Service] 중복된 아이디로 인한 유저 생성 실패: {}", request.getUsername());
            throw DuplicateUserException.withUsername(request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("[Service] 중복된 이메일로 인한 유저 생성 실패: {}", request.getEmail());
            throw DuplicateUserException.withEmail(request.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user;

        if (request.getProfileImage() != null) {
            log.info("[Service] 프로필 이미지 포함 유저 생성");
            BinaryContent profileImage = request.getProfileImage().toBinaryContent();
            binaryContentRepository.save(profileImage);
            eventPublisher.publishEvent(
                    new BinaryContentCreatedEvent(profileImage.getId(), request.getProfileImage().getBytes())
            );
            user = request.toUserWithProfile(encodedPassword, profileImage);
        } else {
            log.info("[Service] 프로필 이미지 없는 유저 생성");
            user = request.toUser(encodedPassword);
        }

        User savedUser = userRepository.save(user);

        List<Channel> publicChannels = channelRepository.findByType(ChannelType.PUBLIC);

        for (Channel channel : publicChannels) {
            ReadStatus readStatus = new ReadStatus(user, channel);
            readStatusRepository.save(readStatus);
        }

        log.info("[Service] 유저 생성 성공: {}", savedUser.getId());
        return UserResponse.success(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        UserResponse userResponse = UserResponse.success(user);
        updateOnlineStatus(List.of(userResponse));
        return userResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        UserResponse userResponse = UserResponse.success(user);
        updateOnlineStatus(List.of(userResponse));
        return userResponse;
    }


    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAllWithProfile();
        List<UserResponse> userResponseList = new ArrayList<>();

        for (User user : users) {
            UserResponse userResponse = UserResponse.success(user);
            userResponseList.add(userResponse);
        }

        updateOnlineStatus(userResponseList);

        return userResponseList;
    }

    @PreAuthorize("#id == authentication.principal.userDto.id()")
    @Override
    @Transactional
    public UserResponse update(UUID id, UserUpdateRequest request,
                               UserProfileImageRequest profileImageRequest) {
        log.info("[Service] 유저 정보 수정 시도");
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        String newUsername = request.getNewUsername();
        String newEmail = request.getNewEmail();

        if (request.getNewUsername() != null && userRepository.existsByUsername(newUsername)) {
            log.info("[Service] 중복된 아이디로 인한 유저 정보 수정 실패: {}", newUsername);
            throw DuplicateUserException.withUsername(newUsername);
        }
        if (request.getNewEmail() != null && userRepository.existsByEmail(newEmail)) {
            log.info("[Service] 중복된 이메일로 인한 유저 정보 수정 실패: {}", newEmail);
            throw DuplicateUserException.withEmail(newEmail);
        }

        if (request.getNewUsername() != null) {
            user.updateUsername(request.getNewUsername());
        }
        if (request.getNewEmail() != null) {
            user.updateEmail(request.getNewEmail());
        }
        if (request.getNewPassword() != null) {
            user.updatePassword(request.getNewPassword());
        }

        if (profileImageRequest != null) {
            BinaryContent newProfileImage = profileImageRequest.toBinaryContent();
            binaryContentRepository.saveAndFlush(newProfileImage);
            eventPublisher.publishEvent(
                    new BinaryContentCreatedEvent(newProfileImage.getId(), profileImageRequest.getBytes())
            );

            if (user.getProfile() != null) {
                binaryContentRepository.deleteById(user.getProfile().getId());
            }

            user.updateProfile(newProfileImage);
        } else {
            if (user.getProfile() != null) {
                binaryContentRepository.deleteById(user.getProfile().getId());
                user.removeProfile();
            }
        }

        userRepository.save(user);
        log.info("[Service] 유저 정보 수정 성공: {}", user.getId());
        return UserResponse.success(user);
    }

    @PreAuthorize("#id == authentication.principal.userDto.id()")
    @Override
    @Transactional
    public UserDeleteResponse delete(UUID id) {
        log.info("[Service] id로 유저 삭제 시도: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> UserNotFoundException.withId(id));

        UUID profileId = null;
        if (user.getProfile() != null) {
            profileId = user.getProfile().getId();
        }

        UserDeleteResponse response = UserDeleteResponse.success(user);

        userRepository.deleteById(user.getId());

        if (profileId != null) {
            binaryContentRepository.deleteById(profileId);
        }

        log.info("[Service] id로 유저 삭제 성공: {}", id);
        return response;
    }

    @Override
    @Transactional
    public UserDeleteResponse delete(String username) {
        log.info("[Service] username으로 유저 삭제 시도: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> UserNotFoundException.withUsername(username));

        UUID profileId = null;
        if (user.getProfile() != null) {
            profileId = user.getProfile().getId();
        }

        UserDeleteResponse response = UserDeleteResponse.success(user);

        userRepository.deleteById(user.getId());

        if (profileId != null) {
            binaryContentRepository.deleteById(profileId);
        }

        log.info("[Service] username으로 유저 삭제 성공: {}", username);
        return response;
    }

    private void updateOnlineStatus(List<UserResponse> userResponses) {
        log.info("[Service] 유저 온라인 상태 업데이트");

        for (UserResponse response : userResponses) {
            boolean online = jwtRegistry.hasActiveJwtInformationByUserId(response.getId());
            response.setOnline(online);
        }
    }
}
