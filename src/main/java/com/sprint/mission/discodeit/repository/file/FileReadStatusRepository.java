package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.AppStorageProperties;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("dev")
public class FileReadStatusRepository extends FileBaseRepository<ReadStatus> implements ReadStatusRepository {

    public FileReadStatusRepository(AppStorageProperties storageProperties) {
        super(ReadStatus.class, storageProperties);
    }
}
