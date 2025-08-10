package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserRequest;
import com.sprint.mission.discodeit.dto.UserFindResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("basicUserService")
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Autowired
    public BasicUserService(UserRepository userRepository,
                            BinaryContentRepository binaryContentRepository,
                            UserStatusRepository userStatusRepository) {
        this.userRepository = userRepository;
        this.binaryContentRepository = binaryContentRepository;
        this.userStatusRepository = userStatusRepository;
    }

    @Override
    public User create(UserRequest request) {
        List<UserFindResponse> users = findAll();
        users.forEach(u -> {
            if(u.username().equals(request.username())) {
                throw new IllegalArgumentException("[Error]: 이름이 중복됩니다.");
            }
            if(u.email().equals(request.email())) {
                throw new IllegalArgumentException("[Error]: 이메일이 중복됩니다.");
            }
        });

        User user = new User(request.username(), request.email(), request.password(), request.profileId());
        userStatusRepository.save(new UserStatus(user.getId(), user.getCreatedAt()));

        return userRepository.save(user);
    }

    @Override
    public UserFindResponse find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        return UserFindResponse.builder().id(user.getId()).username(user.getUsername())
                .email(user.getEmail()).profileId(user.getProfileId())
                .isOnline(userStatusRepository.isOnline(user.getId())).build();
    }

    @Override
    public List<UserFindResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> UserFindResponse.builder()
                .id(user.getId()).username(user.getUsername())
                .email(user.getEmail()).profileId(user.getProfileId())
                .isOnline(userStatusRepository.isOnline(user.getId())).build())
                .toList();
    }

    @Override
    public User update(UserRequest request) {
        User user = userRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("User with id " + request.id() + " not found"));
        user.update(request.username(), request.email(), request.password(), request.profileId());
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }

        binaryContentRepository.deleteById(userRepository.findById(userId).get().getProfileId());
        List<UserStatus> userStatuses =  userStatusRepository.findAll();
        userStatuses.forEach(v -> {
            if(v.getUserId().equals(userId)) {
                userStatusRepository.deleteById(v.getId());
            }
        });

        userRepository.deleteById(userId);
    }
}
