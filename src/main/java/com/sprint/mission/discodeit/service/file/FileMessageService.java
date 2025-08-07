package com.sprint.mission.discodeit.service.file;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.message.DeleteMessageRequest;
import com.sprint.mission.discodeit.dto.request.message.GetMessageRequest;
import com.sprint.mission.discodeit.dto.request.message.GetMessagesByAuthorRequest;
import com.sprint.mission.discodeit.dto.request.message.GetMessagesByChannelIdRequest;
import com.sprint.mission.discodeit.dto.request.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.response.message.DeleteMessageResponse;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelMessageService;
import com.sprint.mission.discodeit.service.MessageService;

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
			if (!message.getAuthorId().equals(authorUUID)) return false;

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

	@Override
	public MessageResponse createMessage(CreateMessageRequest request) {
		return null;
	}

	@Override
	public MessageResponse getMessage(GetMessageRequest request) {
		return null;
	}

	@Override
	public List<MessageResponse> getAllByChannelId(GetMessagesByChannelIdRequest request) {
		return List.of();
	}

	@Override
	public List<MessageResponse> getMessageByAuthor(GetMessagesByAuthorRequest request) {
		return List.of();
	}

	@Override
	public MessageResponse updateMessage(UpdateMessageRequest request) {
		return null;
	}

	@Override
	public DeleteMessageResponse deleteMessage(DeleteMessageRequest request) {
		return null;
	}
}
