package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.request.binaryContent.CreateAttachmentRequest;
import com.sprint.mission.discodeit.dto.request.message.*;
import com.sprint.mission.discodeit.dto.response.message.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.NotChannelMemberException;
import com.sprint.mission.discodeit.exception.UnauthorizedMessageAccessException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
	private final MessageRepository messageRepository;
	private final ChannelRepository channelRepository;
	private final UserRepository userRepository; // 요구사항때문에 일단 넣어두기
	private final BinaryContentRepository binaryContentRepository;


	@Override
	public MessageResponse createMessage(CreateMessageRequest request) {
		Channel channel = channelRepository.findById(request.getChannelId())
			.orElseThrow(ChannelNotFoundException::new);

		if (!channel.getMemberIds().contains(request.getAuthorId())) {
			throw new NotChannelMemberException();
		}

		Message message = new Message(request.getAuthorId(), request.getChannelId(), request.getText());

		addAttachments(message, request.getAttachments());
		messageRepository.save(message);

		return MessageResponse.success(message);
	}

	@Override
	public MessageResponse  getMessage(GetMessageRequest request) {
		Message message = messageRepository.findById(request.getMessageId())
			.orElseThrow(MessageNotFoundException::new);
		return MessageResponse.success(message);
	}

	@Override
	public List<Message> getAllMessages() {
		return messageRepository.findAll();
	}

	@Override
	public List<MessageResponse> getMessageByAuthor(GetMessagesByAuthorRequest request) {
		Channel channel = channelRepository.findById(request.getChannelId())
			.orElseThrow(ChannelNotFoundException::new);

		UUID authorId = null;
		for (Map.Entry<UUID, String> entry : channel.getUserNicknames().entrySet()) {
			if (request.getAuthor().equals(entry.getValue())) {
				authorId = entry.getKey();
				break;
			}
		}

		if (authorId == null) {
			return List.of();
		}

		List<Message> messages = messageRepository.findByAuthorIdAndChannelId(authorId, request.getChannelId());

		return messages.stream()
			.map(MessageResponse::success)
			.toList();
	}

	@Override
	public List<MessageResponse> getAllByChannelId(GetMessagesByChannelIdRequest request) {
		List<Message> messages = messageRepository.findByChannelId(request.getChannelId());

		return messages.stream()
			.map(MessageResponse::success)
			.toList();
	}

	@Override
	public MessageResponse updateMessage(UpdateMessageRequest request) {
		Message message = messageRepository.findById(request.getMessageId())
			.orElseThrow(MessageNotFoundException::new);

		if (!message.getAuthorId().equals(request.getAuthorId())) {
			throw new UnauthorizedMessageAccessException();
		}

		message.updateText(request.getText());

		if (!request.getAttachmentIdsToRemove().isEmpty()) {
			for (UUID attachmentId : request.getAttachmentIdsToRemove()) {
				message.removeAttachment(attachmentId);
				binaryContentRepository.deleteById(attachmentId);
			}
		}

		addAttachments(message, request.getAttachmentsToAdd());
		message.updateUpdatedAt();
		messageRepository.save(message);

		return MessageResponse.success(message);
	}

	@Override
	public DeleteMessageResponse deleteMessage(DeleteMessageRequest request) {
		Message message = messageRepository.findById(request.getMessageId())
			.orElseThrow(MessageNotFoundException::new);

		if (!message.getAuthorId().equals(request.getAuthorId())) {
			throw new UnauthorizedMessageAccessException();
		}

		for (UUID attachmentId : message.getAttachmentIds()) {
			binaryContentRepository.deleteById(attachmentId);
		}

		messageRepository.deleteById(request.getMessageId());

		return DeleteMessageResponse.success(message);
	}

	private void addAttachments(Message message, List<CreateAttachmentRequest> attachments) {
		if (!attachments.isEmpty()) {
			for (CreateAttachmentRequest attachment : attachments) {
				BinaryContent binaryContent = new BinaryContent(
					attachment.getFilename(),
					attachment.getContentType(),
					attachment.getSize(),
					attachment.getContent()
				);
				binaryContentRepository.save(binaryContent);

				message.addAttachment(binaryContent.getId());
			}
		}
	}

	// Deprecated 레거시 코드

	@Override
	public boolean createMessage(UUID authorUUID, UUID channelUUID, String message) {
		return false;
	}

	@Override
	public Message getMessage(UUID messageUUID) {
		return null;
	}

	@Override
	public List<Message> getMessageByAuthor(String targetAuthor, UUID channelUUID) {
		return List.of();
	}

	@Override
	public List<Message> getMessageByChannel(UUID channelUUID) {
		return List.of();
	}

	@Override
	public boolean updateMessage(UUID messageUUID, UUID authorUUID, String text) {
		return false;
	}

	@Override
	public void deleteMessage(UUID messageUUID, UUID authorUUID) {

	}
}
