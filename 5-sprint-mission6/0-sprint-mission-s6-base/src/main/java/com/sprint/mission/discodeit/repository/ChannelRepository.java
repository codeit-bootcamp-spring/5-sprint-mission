package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  // ---- CRUD (JpaRepository가 이미 제공하지만, 과제 요구에 맞춰 명시) ----
  @Override
  Channel save(Channel channel);

  @Override
  Optional<Channel> findById(UUID id);

  @Override
  List<Channel> findAll();

  @Override
  boolean existsById(UUID id);

  @Override
  void deleteById(UUID id);
}
