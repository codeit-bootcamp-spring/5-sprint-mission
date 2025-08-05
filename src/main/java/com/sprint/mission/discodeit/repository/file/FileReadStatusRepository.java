package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.List;
import java.util.UUID;

public class FileReadStatusRepository extends AbstractFileRepository<ReadStatus> implements ReadStatusRepository {
    public FileReadStatusRepository(String filePath) {
        super(filePath, "readstatus");
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return List.of();
    }

    @Override
    public boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        return false;
    }

    @Override
    public void deleteByChannelId(UUID channelId) {

    }
}
