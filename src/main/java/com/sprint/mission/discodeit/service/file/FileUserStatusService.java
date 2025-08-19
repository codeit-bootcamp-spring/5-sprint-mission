package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUserStatusService implements UserStatusService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public void create(UserStatusCreateRequest request) {

        //1. User 존재 확인
        if (userRepository.findById(request.getUserId()) == null) {
            throw new IllegalArgumentException(("해당 유저가 존재하지 않습니다."));
        }

        //2. 중복 확인
        List<UserStatus> all = userStatusRepository.findAll();
        boolean exists = all.stream().anyMatch(s -> s.getUserId().equals(request.getUserId()));
        if (exists) {
            throw new IllegalArgumentException("이미 해당 유저의 상태 정보가 존재합니다.");
        }

        //3. 생성 및 저장
        UserStatus status = new UserStatus(
                UUID.randomUUID(),
                Instant.now(),
                request.getUserId(),
                request.getLastOnline()
        );
        userStatusRepository.save(status);
    }

    @Override
    public UserStatus findById(UUID id) {
        UserStatus status = userStatusRepository.findAll().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (status == null) {
            throw new IllegalArgumentException("해당 ID의 상태 정보가 없습니다.");
        }
        return status;
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public void update(UserStatusUpdateRequest request) {
        UserStatus status = findById(request.getId());
        status.setUpdatedAt(Instant.now());
        status.setLastOnline(request.getLastOnline());

        userStatusRepository.update(status);
    }

    @Override
    public void updateByUserId(UserStatusUpdateRequest request) {
        UserStatus status = userStatusRepository.findAll().stream()
                .filter(s -> s.getUserId().equals(request.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 유저 상태 정보 없음"));

        status.setUpdatedAt(Instant.now());
        status.setLastOnline(request.getLastOnline());

        userStatusRepository.update(status);
    }


    @Override
    public void delete(UUID id) {
        UserStatus status = findById(id); // 존재 확인
        userStatusRepository.delete(id);
    }

    @Override
    public void updateOnlineStatus(UserStatusUpdateRequest request) {
        UserStatus status = userStatusRepository.findByUserId(request.getUserId());
        if (status == null) throw new IllegalArgumentException("유저 상태 없음");
        status.setLastOnline(Instant.now()); // 현재시간으로 온라인 처리
        userStatusRepository.update(status);
    }
}
