package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.UnauthorizedMessageAccessException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelMessageService;
import com.sprint.mission.discodeit.service.MessageService;

public class BasicMessageService implements MessageService {
	private final MessageRepository messageRepository;
	private final ChannelMessageService channelMessageService;

	public BasicMessageService(MessageRepository messageRepository, ChannelMessageService channelMessageService) {
		this.messageRepository = messageRepository;
		this.channelMessageService = channelMessageService;
	}

	@Override
	public boolean createMessage(UUID authorUUID, UUID channelUUID, String message) {
		channelMessageService.createMessage(authorUUID, channelUUID, message);

		return true;
	}

	@Override
	public Message getMessage(UUID messageUUID) {
		return messageRepository.findById(messageUUID)
			.orElseThrow(MessageNotFoundException::new);
	}

	@Override
	public List<Message> getAllMessages() {
		return messageRepository.findAll();
	}

	@Override
	public List<Message> getMessageByAuthor(String targetAuthor, UUID channelUUID) {
		return channelMessageService.getMessageByAuthor(targetAuthor, channelUUID);
	}

	@Override
	public List<Message> getMessageByChannel(UUID channelUUID) {
		return messageRepository.findByChannelId(channelUUID);
	}

	@Override
	public boolean updateMessage(UUID messageUUID, UUID authorUUID, String text) {
		Message message = messageRepository.findById(messageUUID)
			.orElseThrow(MessageNotFoundException::new);

		if (!message.getAuthorUUID().equals(authorUUID)) {
			throw new UnauthorizedMessageAccessException();
		}

		message.updateText(text);
		message.updateUpdatedAt();
		messageRepository.save(message);

		return true;
	}

	@Override
	public void deleteMessage(UUID messageUUID, UUID authorUUID) {
		channelMessageService.deleteMessage(messageUUID, authorUUID);
	}
}
