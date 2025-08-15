package com.sprint.mission.discodeit.service.basic;

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
import com.sprint.mission.discodeit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service("userService")
@RequiredArgsConstructor
@Validated
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User create(@Valid UserCreateRequest userCreateRequest,
                       @Valid Optional<BinaryContentCreateRequest> binaryContentCreateRequest) {
        String username = userCreateRequest.username();
        String password = userCreateRequest.password();
        String email = userCreateRequest.email();
        validateUnique(username, email);

        UUID profileId = binaryContentCreateRequest
                .map(request -> {
                    BinaryContent binaryContent = new BinaryContent(request.fileName(), request.contentType(), request.bytes());
                    return binaryContentRepository.save(binaryContent).getId();
                })
                .orElse(null);

        User user = new User(username, email, password, profileId);

        userStatusRepository.save(new UserStatus(user.getId()));
        return userRepository.save(user);
    }

    @Override
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

    @Override
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

    @Override
    public User update(@Valid UserUpdateRequest userUpdateRequest,
                       @Valid Optional<BinaryContentCreateRequest> binaryContentCreateRequest) {
        String username = userUpdateRequest.username();
        String password = userUpdateRequest.password();
        String email = userUpdateRequest.email();
        validateUnique(username, email);

        User user = userRepository.findById(userUpdateRequest.userId())
                .orElseThrow(() -> new NoSuchElementException("update : 유저를 찾을 수 없습니다."));

        UUID profileId = binaryContentCreateRequest
                .map(request -> {
                    BinaryContent binaryContent = new BinaryContent(request.fileName(), request.contentType(), request.bytes());
                    return binaryContentRepository.save(binaryContent).getId();
                })
                .orElse(null);
        binaryContentRepository.deleteById(user.getProfileId());
        user.update(username, email, password, profileId);

        return userRepository.save(user);
    }

    @Override
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
