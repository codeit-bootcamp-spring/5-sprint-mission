package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.service.dto.message.MessageUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // 신규 권장 API
    Message create(MessageCreateRequest req);
    Message update(MessageUpdateRequest req);
    List<Message> findAllByChannelId(UUID channelId);
    void delete(UUID messageId);

    // 기존 호환 API (변경 없음)
    @Deprecated Message create(String content, UUID channelId, UUID authorId);
    @Deprecated Message find(UUID messageId);
    @Deprecated List<Message> findAll();
    @Deprecated Message update(UUID messageId, String newContent);
}
