package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.*;


public interface MessageRepository {

    Message save(Message message);

    Optional<Message> findById(UUID id);

    List<Message> findAll();

    Message update(UUID id, String content);

    Message delete(UUID id);

    boolean existById(UUID id);



}
