package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  //그 채널에 속한 모든 유저의 읽음정보
  List<ReadStatus> findByChannel_Id(UUID channelId);

  // 채널 삭제시 읽음 기록 제거
  void deleteByChannel_Id(UUID channelId);

  // 특정 유저가 읽은 모든 읽음 상태 조회
  List<ReadStatus> findByUser_Id(UUID userId);

}
