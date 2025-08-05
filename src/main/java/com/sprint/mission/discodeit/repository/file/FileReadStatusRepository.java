package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FileReadStatusRepository extends AbstractFileRepository<ReadStatus> implements ReadStatusRepository {
    public FileReadStatusRepository() {
        super("readStatus");
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {

        return data.values().stream()
            .filter(readStatus -> readStatus.getUserId().equals(userId))
            .collect(Collectors.toList());
    }

    @Override
    public ReadStatus update(UUID id) {
        ReadStatus readStatus = data.get(id);

        if(readStatus == null) {
            return null;
        }

        readStatus.update();
        save(readStatus);

        return readStatus;
    }
}
