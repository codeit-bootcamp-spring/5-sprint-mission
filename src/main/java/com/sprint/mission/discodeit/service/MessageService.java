package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(Message message);
    Message read(UUID id);
    List<Message> readAll();
    boolean update(UUID id, String newContent); // ← boolean 반환
    void delete(UUID id);
}