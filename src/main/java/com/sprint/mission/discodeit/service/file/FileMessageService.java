package com.sprint.mission.discodeit.service.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelMessageService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

public class FileMessageService implements MessageService {
	private final MessageRepository messageRepository;
	private final ChannelMessageService channelMessageService;

	public FileMessageService(MessageRepository messageRepository, ChannelMessageService channelMessageService) {
		this.messageRepository = messageRepository;
		this.channelMessageService = channelMessageService;
	}

	@Override
	public boolean createMessage(UUID authorUUID, UUID channelUUID, String text) {
		if(authorUUID == null || channelUUID == null || text == null) return false;

		channelMessageService.createMessage(authorUUID, channelUUID, text);
		return true;
	}

	@Override
	public Message getMessage(UUID messageUUID) {
		return messageRepository.findById(messageUUID).orElse(null);
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
		if (messageUUID == null || authorUUID == null || text == null) return false;

		Optional<Message> messageOpt = messageRepository.findById(messageUUID);
		if (messageOpt.isPresent()) {
			Message message = messageOpt.get();
			if (!message.getAuthorUUID().equals(authorUUID)) return false;

			message.updateText(text);
			message.updateUpdatedAt();
			messageRepository.save(message);
			return true;
		}
		return false;
	}



	@Override
	public void deleteMessage(UUID messageUUID, UUID authorUUID) {
		channelMessageService.deleteMessage(messageUUID, authorUUID);
	}
}
