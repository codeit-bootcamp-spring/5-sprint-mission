package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.AppStorageProperties;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
@Profile("dev")
public class FileBinaryContentRepository extends FileBaseRepository<BinaryContent> implements BinaryContentRepository {

    public FileBinaryContentRepository(AppStorageProperties storageProperties) {
        super(BinaryContent.class, storageProperties);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(Set<UUID> ids) {
        return findAllByIds(ids);
    }
}
