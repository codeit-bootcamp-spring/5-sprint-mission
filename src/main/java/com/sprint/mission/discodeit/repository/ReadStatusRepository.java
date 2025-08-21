package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.*;


public interface ReadStatusRepository {

    /**
     * 읽음 상태 저장 또는 갱신
     */
    void save(ReadStatus readStatus);

    void saveAll(List<ReadStatus> readStatuses);

    List<ReadStatus> findByUserId(UUID userId);

    ReadStatus findById(UUID readStatusId);

    /**
     * 특정 사용자-채널 조합의 읽음 상태 조회
     */
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);


    /**
     * 특정 사용자의 모든 채널 읽음 상태 조회
     */
    List<ReadStatus> findAllByUserId(UUID userId);


    /**
     * 특정 채널의 모든 사용자 읽음 상태 조회
     */
    List<ReadStatus> findAllByChannelId(UUID channelId);

    /**
     * 특정 채널의 읽음 상태
     */
    Optional<ReadStatus> findByChannelId(UUID channelId);
}
