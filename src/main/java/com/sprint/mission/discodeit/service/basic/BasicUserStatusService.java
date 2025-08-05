package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    UserStatusRepository userStatusRepository;

    @Override
    public UserStatus create(UserStatus readStatus) {
        return userStatusRepository.save(readStatus);
    }

    @Override
    public UserStatus find(UUID id) {
        return userStatusRepository.findById(id).orElse(null);
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UUID id) {
        return userStatusRepository.update(id);
    }

    @Override
    public UserStatus updateByUserId(UUID userId) {
        return userStatusRepository.updateByUserId(userId);
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.delete(id);
    }
}
