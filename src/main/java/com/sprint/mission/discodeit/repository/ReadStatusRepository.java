package com.sprint.mission.discodeit.repository; // 레포지토리 패키지 선언

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    ReadStatus save(ReadStatus readStatus);
    Optional<ReadStatus> findById(UUID id);
    List<ReadStatus> findAllByUserId(UUID userId);
    List<ReadStatus> findAllByChannelId(UUID channelId);
    boolean existsById(UUID id);
    void deleteById(UUID id);
    void deleteAllByChannelId(UUID channelId);

}
