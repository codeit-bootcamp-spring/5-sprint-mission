package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto create(MessageCreateRequest messageCreateRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests);
    MessageDto find(UUID id);
    PageResponse<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable);
    MessageDto update(UUID id, MessageUpdateRequest request);
    void delete(UUID id);
}
