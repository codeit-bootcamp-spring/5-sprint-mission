package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.message.*;
import com.sprint.mission.discodeit.dto.response.message.*;
import com.sprint.mission.discodeit.entity.Message;

public interface MessageService {
	// 생성
	MessageResponse createMessage(CreateMessageRequest request);

	// 읽기
	MessageResponse getMessage(GetMessageRequest request);
	List<MessageResponse> getAllByChannelId(GetMessagesByChannelIdRequest request);
	List<MessageResponse> getMessageByAuthor(GetMessagesByAuthorRequest request);

	// 수정
	MessageResponse updateMessage(UpdateMessageRequest request);

	// 삭제
	DeleteMessageResponse deleteMessage(DeleteMessageRequest request);
}
