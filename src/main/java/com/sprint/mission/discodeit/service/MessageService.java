package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message sendMessage(String message);
    Message find(UUID id);
    Message findAll();
    Message update(UUID id, String message);
    boolean delete(UUID id);
}
