package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

  // ---- CRUD (JpaRepository 기본 제공이지만 과제 요구에 맞춰 명시) ----
  @Override
  BinaryContent save(BinaryContent binaryContent);

  @Override
  Optional<BinaryContent> findById(UUID id);

  @Override
  boolean existsById(UUID id);

  @Override
  void deleteById(UUID id);

  List<BinaryContent> findAllByIdIn(List<UUID> ids);
}