package com.sprint.mission.discodeit.respository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.respository.BinaryContentRepository;

import java.util.*;

public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> data = new HashMap<>();

    @Override
    public void save(BinaryContent binaryContent) {
        data.put(binaryContent.getId(), binaryContent);
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<BinaryContent> findAll() {
        return List.copyOf(data.values());
    }
}
