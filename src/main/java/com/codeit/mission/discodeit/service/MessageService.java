package com.codeit.mission.discodeit.service;

import com.codeit.mission.discodeit.dto.data.MessageDto;
import com.codeit.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.codeit.mission.discodeit.dto.request.MessageCreateRequest;
import com.codeit.mission.discodeit.dto.request.MessageUpdateRequest;
import com.codeit.mission.discodeit.dto.response.PageResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    MessageDto create(MessageCreateRequest messageCreateRequest,
            List<BinaryContentCreateRequest> binaryContentCreateRequests);

    MessageDto find(UUID messageId);

    PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createdAt,
            Pageable pageable);

    MessageDto update(UUID messageId, MessageUpdateRequest request);

    void delete(UUID messageId);
}
