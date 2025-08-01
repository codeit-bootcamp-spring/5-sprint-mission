package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Message;

public interface ChannelMessageService {
	void createMessage(UUID authorUUID, UUID channelUUID, String text);
	List<Message> getMessageByAuthor(String targetAuthor, UUID channelUUID);
	void deleteMessage(UUID messageUUID, UUID authorUUID);
	void deleteChannelWithMessages(UUID channelUUID);
}
