package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;



import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  Slice<Message> findAllByChannel_Id(UUID channelId, Pageable pageable);

  void deleteAllByChannel_Id(UUID channelId);


}
