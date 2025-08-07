package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> storage = new HashMap<>();

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        storage.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(storage::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID id) {
        return storage.containsKey(id);
    }

    @Override
    public boolean deleteById(UUID id) {
        return storage.remove(id) != null;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        storage.values().removeIf(b -> userId.equals(b.getOwnerId()));
    }

    @Override
    public boolean deleteAllByMessageId(UUID messageId) {
        // BinaryContent가 메시지와 직접 연결되어 있지 않다면, false만 리턴
        return false;
    }
}
