package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessagesGetByAuthorRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageDeleteResponse;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.message.UnauthorizedMessageAccessException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
	private final MessageRepository messageRepository;
	private final ChannelRepository channelRepository;
	private final UserRepository userRepository;
	private final BinaryContentRepository binaryContentRepository;



	@Override
    @Transactional
	public MessageResponse createMessage(MessageCreateRequest request) {
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getAuthorId()));

		Channel channel = channelRepository.findById(request.getChannelId())
				.orElseThrow(ChannelNotFoundException::new);

		Message message = new Message(author, channel, request.getContent());

		addAttachments(message, request.getAttachments());
		messageRepository.save(message);

		return MessageResponse.success(message);
	}

	@Override
    @Transactional(readOnly = true)
	public MessageResponse findMessage(UUID messageId) {
		Message message = messageRepository.findById(messageId)
			.orElseThrow(MessageNotFoundException::new);
		return MessageResponse.success(message);
	}

	@Override
    @Transactional(readOnly = true)
	public List<Message> getAllMessages() {
		return messageRepository.findAll();
	}

	@Override
	public List<MessageResponse> findMessageByAuthor(MessagesGetByAuthorRequest request) {
		Channel channel = channelRepository.findById(request.getChannelId())
				.orElseThrow(ChannelNotFoundException::new);

		UUID authorId = null;
		List<User> allUsers = userRepository.findAll();

		for (User user : allUsers) {
			if (request.getAuthor().equals(user.getUsername())) {
				authorId = user.getId();
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
    @Transactional(readOnly = true)
	public List<MessageResponse> findMessagesByChannelId(UUID channelId) {
		List<Message> messages = messageRepository.findByChannelId(channelId);

		return messages.stream()
			.map(MessageResponse::success)
			.toList();
	}

	@Override
    @Transactional
	public MessageResponse updateMessage(UUID messageId, MessageUpdateRequest request) {
		Message message = messageRepository.findById(messageId)
				.orElseThrow(MessageNotFoundException::new);

		if (!message.getAuthor().getId().equals(request.getAuthorId())) {
			throw new UnauthorizedMessageAccessException();
		}

		message.setContent(request.getContent());

		if (!request.getAttachmentIdsToRemove().isEmpty()) {
			for (UUID attachmentId : request.getAttachmentIdsToRemove()) {
				message.removeAttachment(attachmentId);
				binaryContentRepository.deleteById(attachmentId);
			}
		}

		addAttachments(message, request.getAttachmentsToAdd());
		messageRepository.save(message);

		return MessageResponse.success(message);
	}

	@Override
    @Transactional
	public MessageDeleteResponse deleteMessage(UUID messageId, UUID authorId) {
		Message message = messageRepository.findById(messageId)
			.orElseThrow(MessageNotFoundException::new);

		if (!message.getAuthor().getId().equals(authorId)) {
			throw new UnauthorizedMessageAccessException();
		}

		for (MessageAttachment attachment : message.getAttachments()) {
            UUID attachmentId = attachment.getAttachment().getId();
			binaryContentRepository.deleteById(attachmentId);
		}

		messageRepository.deleteById(messageId);

		return MessageDeleteResponse.success(message);
	}

	private void addAttachments(Message message, List<BinaryContentCreateRequest> attachments) {
		if (attachments != null && !attachments.isEmpty()) {
			for (BinaryContentCreateRequest attachment : attachments) {
				BinaryContent binaryContent = new BinaryContent(
					attachment.getFileName(),
					attachment.getContentType(),
					attachment.getSize()
				);
				binaryContentRepository.save(binaryContent);

				message.addAttachment(binaryContent);
			}
		}
	}
}
