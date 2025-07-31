package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Message;

public interface MessageService {
	// 생성
	boolean createMessage(UUID authorUUID, UUID channelUUID,String message);

	// 읽기
	Message getMessage(UUID messageUUID);
	List<Message> getMessageAll();
	List<Message> getMessageByAuthor(String targetAuthor, UUID channelUUID);
	List<Message> getMessageByChannel(UUID channelUUID);

	// 수정
	boolean updateMessage(UUID messageUUID, UUID authorUUID, String text);

	// 삭제
	void deleteMessage(UUID messageUUID, UUID authorUUID);

}
