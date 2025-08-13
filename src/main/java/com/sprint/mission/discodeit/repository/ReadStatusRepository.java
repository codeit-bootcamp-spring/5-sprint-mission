package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;


public interface ReadStatusRepository {

    void save(ReadStatus readStatus);

    ReadStatus findById(UUID id); // 단건 조회

    List<ReadStatus> findByChannelId(UUID channelId); // 채널 참가자들 읽음 조회

    void deleteByChannelId(UUID channelId); // 채널 삭제시 읽음 기록 제거

    void deleteById(UUID id); // 단건 읽음 삭제

    List<ReadStatus> findAll(); // 전체 조회


}
