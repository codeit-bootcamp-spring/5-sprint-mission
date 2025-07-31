package com.sprint.mission.discodeit.respository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.respository.ReadStatusRepository;
import java.util.*;

public class FileReadStatusRepository extends FileStore<ReadStatus> implements ReadStatusRepository {

    private final Map<UUID, ReadStatus> data = new HashMap<>();

    public FileReadStatusRepository(String rootDir) {
        super(rootDir + "readStatus.store");
        Map<UUID, ReadStatus> loaded = loadFromFile();
        if (loaded != null) {
            data.putAll(loaded);
        }
    }

    // 메시지 읽은 시각 저장 / 갱신
    @Override
    public void save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);
        saveToFile(data);
    }

    // 모든 채널의 메시지 상태
    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus readStatus : data.values()) {
            if (readStatus.getChannelId().equals(channelId)) {
                result.add(readStatus);
            }
        }
        return result;
    }

    // 특정 채널의 메시지 상태
    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        for (ReadStatus readStatus : data.values()) {
            if (readStatus.getUserId().equals(userId) && readStatus.getChannelId().equals(channelId)) {
                return Optional.of(readStatus);
            }
        }
        return Optional.empty();
    }


}
