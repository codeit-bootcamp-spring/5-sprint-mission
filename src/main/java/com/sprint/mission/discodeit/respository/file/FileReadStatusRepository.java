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

    // 읽음 상태 저장 / 갱신
    @Override
    public void save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);
        saveToFile(data);
    }

    // 특정 채널에 속한 모든 유저의 읽음 상태 조회
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

    /**
     * 특정 유저가 속한 채널 중 하나에 대한 읽음 상태 조회
     */
    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return data.values().stream()
                .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
                .findFirst();
    }

    /**
     * 특정 유저가 가진 모든 채널의 읽음 상태 조회
     */
    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus readStatus : data.values()) {
            if (readStatus.getUserId().equals(userId)) {
                result.add(readStatus);
            }
        }
        return result;
    }


}
