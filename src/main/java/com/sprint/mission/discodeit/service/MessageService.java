package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message create(MessageCreateDto dto);

    List<Message> findAll();

    Message findById(UUID id);

    List<Message> findByUserId(UUID userId);

    List<Message> findByContent(String content);

    Message update(UUID messageId, String message);

    void attachFile(UUID messageId, UUID fileId);

    void detachFile(UUID messageId, UUID fileId);

    boolean delete(UUID id);
}
