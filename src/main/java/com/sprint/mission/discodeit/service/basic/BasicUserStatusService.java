package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    /**
     * 파라미터 값에 오는 userId는 모두 존재한다고 가정
     * */

    @Override
    public boolean isActive(UUID userId) {
        UserStatus status = userStatusRepository.findByUserId(userId);

        return status.isActive();
    }

    @Override
    public void updateLastActive(User user) {
        UserStatus status = userStatusRepository.findByUserId(user.getId());

        status.updateLastActive();
        userStatusRepository.save(status);
    }

    @Override
    public void create(UserStatusCreateDto dto) {

    }

    @Override
    public void findById(UUID id) {

    }

    @Override
    public void findAllByUserId(UUID userId) {

    }

    @Override
    public void deleteById(UUID id) {

    }
}
