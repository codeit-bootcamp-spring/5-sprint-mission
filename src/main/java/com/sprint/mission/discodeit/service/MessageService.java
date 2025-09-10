package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(MessageCreateRequest messageCreateRequest,
                   List<BinaryContentCreateRequest> binaryContentCreateRequests);

    Message find(UUID messageId);

    PageResponse<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable);

    Message update(UUID messageId, MessageUpdateRequest request);

    void delete(UUID messageId);
}
