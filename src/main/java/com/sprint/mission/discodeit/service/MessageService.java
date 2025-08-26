package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.message.*;
import com.sprint.mission.discodeit.dto.response.message.*;
import com.sprint.mission.discodeit.entity.Message;

public interface MessageService {
	// 생성
	MessageResponse createMessage(MessageCreateRequest request);

	// 읽기
	MessageResponse findMessage(UUID messageId);
	List<MessageResponse> findMessagesByChannelId(UUID channelId);
	List<MessageResponse> findMessageByAuthor(MessagesGetByAuthorRequest request);

	List<Message> getAllMessages();

	// 수정
	MessageResponse updateMessage(UUID messageId, MessageUpdateRequest request);

	// 삭제
	MessageDeleteResponse deleteMessage(UUID messageId, UUID authorId);
}
