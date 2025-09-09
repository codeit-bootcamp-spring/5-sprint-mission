package com.sprint.mission.discodeit.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.message.*;
import com.sprint.mission.discodeit.dto.response.page.PageOffsetResponse;
import com.sprint.mission.discodeit.dto.response.page.PageResponse;
import com.sprint.mission.discodeit.dto.response.message.*;
import com.sprint.mission.discodeit.entity.Message;

public interface MessageService {
	// 생성
	MessageResponse create(MessageCreateRequest request);

	// 읽기
	MessageResponse findMessage(UUID messageId);
	List<MessageResponse> findMessagesByChannelId(UUID channelId);
	List<MessageResponse> findMessageByAuthor(MessagesGetByAuthorRequest request);

    PageOffsetResponse<MessageResponse> findPageMessagesByChannel(UUID channelId, int page, int size, String[] sort);
    PageOffsetResponse<MessageResponse> findSliceMessagesByChannel(UUID channelId, int page, int size, String[] sort);
    PageResponse<MessageResponse> findMessagesByChannelWithCursor(UUID channelId, Instant cursor, int size, String sort);

	List<Message> findAll();

	// 수정
	MessageResponse updateMessage(UUID messageId, MessageUpdateRequest request);

	// 삭제
	MessageDeleteResponse deleteMessage(UUID messageId, UUID authorId);
}
