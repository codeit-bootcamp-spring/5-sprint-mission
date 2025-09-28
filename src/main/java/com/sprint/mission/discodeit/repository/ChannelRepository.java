package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
<<<<<<< HEAD
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channel);
    Optional<Channel> findById(UUID id);
    List<Channel> findAll();
//    List<Channel> findAllPublicChannels();
    boolean existsById(UUID id);
//    boolean existsByName(String name);
    void deleteById(UUID id);
//    List<Channel> findByUserId(UUID userId);
=======
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  List<Channel> findAllByTypeOrIdIn(ChannelType type, List<UUID> ids);
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
}
