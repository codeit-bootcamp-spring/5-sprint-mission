package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    void save(Message message) throws IOException;
    Message findById(UUID id) throws IOException, ClassNotFoundException;
    Message findByContent(String content) throws IOException, ClassNotFoundException;
    List<Message> findAll() throws IOException, ClassNotFoundException;
    void update(Message message) throws IOException;
    void delete(UUID id) throws IOException;
}
