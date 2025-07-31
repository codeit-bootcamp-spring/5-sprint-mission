package com.sprint.mission.discodeit.respository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.*;


public interface ReadStatusRepository {

    // 메시지 읽은 시각 저장 / 갱신
    void save(ReadStatus readStatus);

    // 모든 채널의 메시지 상태
    List<ReadStatus> findAllByChannelId(UUID channelId); // 선택

    // 특정 채널의 메시지 상태
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);



}
