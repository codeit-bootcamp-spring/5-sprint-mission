package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    BinaryContent save(BinaryContent binaryContent);
    public BinaryContent save(BinaryContent binaryContent, User user);
    public BinaryContent save(BinaryContent binaryContent, Message message);
    Optional<BinaryContent> findById(UUID id);
    List<BinaryContent> findAllByIdIn(List<UUID> ids);
    List<BinaryContent> getAllData();
    boolean existsById(UUID id);
    void deleteById(UUID id);
}
