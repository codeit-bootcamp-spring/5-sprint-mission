package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileReadStatusRepository extends FileStore<ReadStatus> implements ReadStatusRepository {

    private final Map<UUID, ReadStatus> data = new HashMap<>();

    public FileReadStatusRepository(@Value("${discodeit.repository.file-directory:.discodeit}") String rootDir) {
        super(rootDir + "readStatus.ser");
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

    @Override
    public void saveAll(List<ReadStatus> readStatuses) {
        if (readStatuses == null || readStatuses.isEmpty()) return;

        readStatuses.forEach(rs -> data.put(rs.getId(), rs));

        saveToFile(data);
    }

    @Override
    public ReadStatus findById(UUID readStatusId) {
        return data.get(readStatusId);
    }

    @Override
    public List<ReadStatus> findByUserId(UUID userId) {
        return data.values().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .toList();
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

    @Override
    public Optional<ReadStatus> findByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .findFirst();
    }


}
