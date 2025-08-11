package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service("basicUserStatusService")
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserStatus create(UserStatusDto.CreateUserStatus dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new NoSuchElementException("찾을 수 없는 사용자입니다" + dto.userId()));

        if (userStatusRepository.findByUserId(dto.userId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다 " + dto.userId());
        }

        UserStatus status = new UserStatus(dto.userId(), Instant.now());
        return userStatusRepository.save(status);
    }
    @Override
    public UserStatus find(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다: " + id));
    }
    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }
    @Override
    public UserStatus update(UserStatusDto.UpdateUserStatus dto) {
        UserStatus userStatus = userStatusRepository.findById(dto.id())
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        userStatus.updateLastAccessAt(dto.newAccessTime());
        return userStatusRepository.save(userStatus);
    }
}
