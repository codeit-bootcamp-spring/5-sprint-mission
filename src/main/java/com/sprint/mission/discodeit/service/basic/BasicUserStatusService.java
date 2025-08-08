package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserStatus addUserStatus(UUID userId) {
        userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("BasicUserStatusService: User not found"));
        userStatusRepository.findByUserId(userId)
                .ifPresent(userStatus -> {throw new IllegalArgumentException("BasicUserStatusService: UserStatus already exists");});

        UserStatus userStatus = new UserStatus(userId);
        return userStatusRepository.save(userStatus)
                .orElseThrow(() -> new RuntimeException("Failed to save UserStatus"));
    }

    @Override
    public UserStatus getUserStatusById(UUID id){
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("BasicUserStatusService: UserStatus not found"));
    }

    @Override
    public List<UserStatus> getAllUserStatus() {
        return userStatusRepository.findAll();
    }

    @Override
    public void deleteAllUserStatus() {
        userStatusRepository.deleteAll();
    }

    @Override
    public UserStatus updateUserStatus(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("BasicUserStatusService: UserStatus not found"));

        userStatus.updateLastOnlineTime();

        return userStatusRepository.save(userStatus)
                .orElseThrow(() -> new RuntimeException("Failed to update UserStatus"));
    }

    @Override
    public UserStatus updateUserStatusByUserId(UUID userId){
        userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("BasicUserStatusService: User not found"));

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("BasicUserStatusService: UserStatus not found"));

        userStatus.updateLastOnlineTime();

        return userStatusRepository.save(userStatus)
                .orElseThrow(() -> new RuntimeException("Failed to update UserStatus"));
    }

    @Override
    public void deleteUserStatus(UUID userStatusId){
        userStatusRepository.deleteById(userStatusId);
    }
}
