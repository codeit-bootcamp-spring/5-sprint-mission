package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(Message message);

    Message create(String text, UUID channelId, UUID userId);

    List<Message> getAll();

    Message get(UUID id);

    Message update(UUID id, String text);

    // TODO mission 3 인터페이스 정리 예정 : create, find, findall, update, delete

    MessageDto.DetailResponse create(MessageDto.CreateRequest request);

    MessageDto.DetailResponse update(MessageDto.UpdateRequest request);

    MessageDto.DetailResponse findById(UUID id);

    List<MessageDto.DetailResponse> findAllByChannelId(UUID channelId);

    void delete(UUID id);

    void deleteAll();
}
