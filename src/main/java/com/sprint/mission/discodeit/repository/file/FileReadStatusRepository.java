package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.common.FileUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path READ_STATUS_DIR = Path.of(ReadStatus.class.getSimpleName());

    public FileReadStatusRepository() {
        FileUtils.init(READ_STATUS_DIR);
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path path = READ_STATUS_DIR.resolve(readStatus.getId().toString());
        FileUtils.save(path, readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        Path path = READ_STATUS_DIR.resolve(id.toString());
        return Optional.ofNullable(FileUtils.findOne(path, ReadStatus.class));
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return FileUtils.findAll(READ_STATUS_DIR, ReadStatus.class).stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return FileUtils.findAll(READ_STATUS_DIR, ReadStatus.class).stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public void delete(UUID id) {
        Path path = READ_STATUS_DIR.resolve(id.toString());
        FileUtils.delete(path);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        FileUtils.findAll(READ_STATUS_DIR, ReadStatus.class).stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .map(ReadStatus::getId)
                .forEach(this::delete);
    }
}
