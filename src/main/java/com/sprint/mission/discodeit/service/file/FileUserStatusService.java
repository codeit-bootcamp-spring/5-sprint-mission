package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileUserStatusService implements UserStatusService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;

  //상태 업데이트
  @Override
  public void updateUserStatusByUserId(UUID userId, UserStatusUpdateRequest request) {
    UserStatus status = userStatusRepository.findAll().stream()
        .filter(s -> s.getUserId().equals(userId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("해당 유저 상태 정보 없음"));

    status.setUpdatedAt(Instant.now());
    status.setLastOnline(request.getLastOnline());

    userStatusRepository.update(status);
  }

  //단건조회
  @Override
  public UserStatus findByUserId(UUID userId) {
    UserStatus status = userStatusRepository.findByUserId(userId.toString());
    if (status == null) {
      throw new IllegalArgumentException("해당 유저의 상태 정보가 없습니다.");
    }
    return status;
  }


  //상태 삭제
  @Override
  public void delete(UUID userId) {
    UserStatus status = findByUserId(userId); // 존재 확인
    userStatusRepository.delete(userId);
  }

  @Override
  public List<UserStatus> findAll() {
    return List.of();
  }
}
