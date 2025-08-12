package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("userStatusService")
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(UserStatusCreateRequest request) {

        // 관련된 User가 존재하지 않으면 예외를 발생시킵니다.
        // 같은 User와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
        if(userRepository.findById(request.userId()).isEmpty()) {
            throw new IllegalStateException("User with id " + request.userId() + " not found");
        }

        UserStatus userStatus = new UserStatus(request.userId());
        userStatusRepository.save(userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId);
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(request.userStatusId()).orElseThrow(
                () -> new IllegalStateException("User with id " + request.userStatusId() + " not found")
        );
        userStatus.update();
        return userStatus;
    }

    @Override
    public void delete(UUID userStatusId) {
        userStatusRepository.delete(userStatusId);
    }
}
