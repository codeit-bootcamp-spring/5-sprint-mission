package com.sprint.mission.discodeit.service;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.Message;

public interface MessageService {
	// 생성
	boolean createMessage(UUID authorUUID, UUID Channel,String message);

	// 읽기
	Message getMessage(UUID id);
	Message[] getMessageAll();
	Message[] getMessageByAuthorUUID(UUID authorUUID);
	Message[] getMessageByChannelUUID(UUID channelUUID);
	Message[] getMessageByAuthorUUIDAndChannelUUID(UUID authorUUID, UUID channelUUID);

	// 수정
	boolean updateMessage(UUID id, String message);

	// 삭제
	boolean deleteMessageAll(UUID id);
	boolean deleteMessageByAuthorUUID(UUID id, UUID authorUUID);
	boolean deleteMessageByChannelUUID(UUID id, UUID channelUUID);
	boolean deleteMessageByAuthorUUIDAndChannelUUID(UUID id,UUID authorUUID, UUID channelUUID);
}
