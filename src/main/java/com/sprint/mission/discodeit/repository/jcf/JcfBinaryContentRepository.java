package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
@Profile("test")
public class JcfBinaryContentRepository extends JcfBaseRepository<BinaryContent> implements BinaryContentRepository {

    @Override
    protected String getEntityTypeName() {
        return "BinaryContent";
    }

    @Override
    public List<BinaryContent> findAllByIdIn(Set<UUID> ids) {
        return findAllByIds(ids);
    }
}
