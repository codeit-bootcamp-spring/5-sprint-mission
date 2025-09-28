package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
<<<<<<< HEAD
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    BinaryContent save(BinaryContent binaryContent);
    Optional<BinaryContent> findById(UUID id);
    List<BinaryContent> findAllByIdIn(List<UUID> ids);
//    List<BinaryContent> findAllByMessageId(UUID messageId);
//    void deleteByMessageId(UUID messageId);
    boolean existsById(UUID id);
    void deleteById(UUID id);
=======
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
}
