package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserFindResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service("userService")
@RequiredArgsConstructor
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    public User create(@Valid UserCreateRequest userRequest,
                       @Valid Optional<BinaryContentCreateRequest> binaryRequest) {
        String username = userRequest.username();
        String password = userRequest.password();
        String email = userRequest.email();
        validateUnique(username, email);

        UUID profileId = binaryRequest
                .map(request -> {
                    BinaryContent binaryContent = new BinaryContent(request.fileName(), request.contentType(), request.bytes());
                    return binaryContentRepository.save(binaryContent).getId();
                })
                .orElse(null);

        User user = new User(username, email, password, profileId);

        userStatusRepository.save(new UserStatus(user.getId()));
        return userRepository.save(user);
    }

    public UserFindResponse findById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("findById : 유저를 찾을 수 없습니다."));

        return UserFindResponse.builder()
                .id(user.getId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileId(user.getProfileId())
                .online(userStatusRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new NoSuchElementException("findById : UserStatus를 찾을 수 없습니다.")).isLogin())
                .build();
    }

    public List<UserFindResponse> findAll() {
        List<UserFindResponse> userFindResponses = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new NoSuchElementException("findAll : UserStatus를 찾을 수 없습니다."));
            userFindResponses.add(UserFindResponse.builder()
                    .id(user.getId())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .profileId(user.getProfileId())
                    .online(userStatus.isLogin())
                    .build());
        }
        return userFindResponses;
    }

    public User update(@Valid UserUpdateRequest userRequest,
                       @Valid Optional<BinaryContentCreateRequest> binaryRequest) {
        String username = userRequest.username();
        String password = userRequest.password();
        String email = userRequest.email();
        validateUnique(username, email);

        User user = userRepository.findById(userRequest.userId())
                .orElseThrow(() -> new NoSuchElementException("update : 유저를 찾을 수 없습니다."));

        UUID profileId = binaryRequest
                .map(request -> {
                    BinaryContent binaryContent = new BinaryContent(request.fileName(), request.contentType(), request.bytes());
                    return binaryContentRepository.save(binaryContent).getId();
                })
                .orElse(null);

        user.update(username, email, password, profileId);

        return userRepository.save(user);
    }

    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("delete : 유저를 찾을 수 없습니다."));

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }

        userStatusRepository.findByUserId(user.getId()).ifPresent(userStatus -> userStatusRepository.deleteById(userStatus.getId()));

        userRepository.deleteById(user.getId());
    }

    public void deleteAll() {
        userRepository.findAll().forEach(user -> delete(user.getId()));
    }

    private void validateUnique(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("validateUnique : 이미 존재하는 username 입니다.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("validateUnique : 이미 존재하는 email 입니다.");
        }
    }
}
