package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

  //특정 userId를 가진 유저의 온라인 상태 조회
  Optional<UserStatus> findByUser_Id(UUID userId);

}
