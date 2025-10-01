package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessagesGetByAuthorRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageDeleteResponse;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.dto.response.page.PageOffsetResponse;
import com.sprint.mission.discodeit.dto.response.page.PageResponse;
import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    // 생성
    MessageResponse create(MessageCreateRequest request);

    // 읽기
    MessageResponse findMessage(UUID messageId);

    List<MessageResponse> findMessagesByChannelId(UUID channelId);

    List<MessageResponse> findMessageByAuthor(MessagesGetByAuthorRequest request);

    PageResponse<MessageResponse> findMessagesByChannelWithCursor(UUID channelId, Instant cursor, int size, String sort);

    List<Message> findAll();

    // 수정
    MessageResponse updateMessage(UUID messageId, MessageUpdateRequest request);

    // 삭제
    MessageDeleteResponse deleteMessage(UUID messageId, UUID authorId);
}
