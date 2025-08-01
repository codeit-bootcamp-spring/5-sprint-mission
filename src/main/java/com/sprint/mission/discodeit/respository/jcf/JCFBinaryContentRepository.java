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
        return data.values().stream()
                .filter(binaryContent -> binaryContent.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(data.values());
    }
}
