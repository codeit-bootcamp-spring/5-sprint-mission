package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.UUID;


public interface ReadStatusRepository {

  void save(ReadStatus readStatus);

  List<ReadStatus> findByChannelId(UUID channelId); //그 채널에 속한 모든 유저의 읽음정보

  void deleteByChannelId(UUID channelId); // 채널 삭제시 읽음 기록 제거

  List<ReadStatus> findAll(); // 전체 조회

  ReadStatus findById(UUID id); // 단건 조회

}
