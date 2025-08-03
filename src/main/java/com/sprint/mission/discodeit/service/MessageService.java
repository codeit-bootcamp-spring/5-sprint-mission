package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    void create(Message message) throws IOException;
    Message get(UUID id) throws IOException, ClassNotFoundException;
    Message get(String content) throws IOException, ClassNotFoundException;
    List<Message> getAll() throws IOException, ClassNotFoundException;
    void update(Message message) throws IOException;
    void delete(UUID id) throws IOException;
}
