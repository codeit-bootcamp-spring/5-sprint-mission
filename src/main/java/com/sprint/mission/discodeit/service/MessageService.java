package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponse create(MessageCreateRequest request);

    MessageResponse find(UUID messageId);

    List<MessageResponse> findAll();

    List<MessageResponse> findAllByChannelId(UUID channelId);

    MessageResponse update(MessageUpdateRequest request);

    void delete(UUID messageId);

    /**
     * 모든 메시지 데이터를 초기화합니다.
     * 테스트 환경에서 사용됩니다.
     */
    void clear();
}
