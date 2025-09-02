package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessagesGetByAuthorRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.response.message.MessageDeleteResponse;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.message.UnauthorizedMessageAccessException;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
	private final MessageRepository messageRepository;
	private final ChannelRepository channelRepository;
	private final UserRepository userRepository;
	private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final PageResponseMapper pageResponseMapper;



	@Override
    @Transactional
	public MessageResponse create(MessageCreateRequest request) {
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getAuthorId()));

		Channel channel = channelRepository.findById(request.getChannelId())
				.orElseThrow(ChannelNotFoundException::new);

		Message message = new Message(author, channel, request.getContent());

        messageRepository.save(message);
		addAttachments(message, request.getAttachments());


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
	public List<Message> findAll() {
		return messageRepository.findAll();
	}



	@Override
    @Transactional(readOnly = true)
	public List<MessageResponse> findMessageByAuthor(MessagesGetByAuthorRequest request) {
		Channel channel = channelRepository.findById(request.getChannelId())
				.orElseThrow(ChannelNotFoundException::new);

        User author = userRepository.findByUsername(request.getAuthor())
                .orElse(null);

        if (author == null) {
            return List.of();
        }

        List<Message> messages = messageRepository.findByAuthorIdAndChannelId(author.getId(), request.getChannelId());

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
    @Transactional(readOnly = true)
    public PageResponse<MessageResponse> findPageMessagesByChannel(UUID channelId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Message> messagePage = messageRepository.findPageByChannelId(channelId, pageable);
        Page<MessageResponse> responsePage = messagePage.map(MessageResponse::success);
        return pageResponseMapper.fromPage(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageResponse> findSliceMessagesByChannel(UUID channelId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Slice<Message> messageSlice = messageRepository.findByChannelId(channelId, pageable);

        // Message → DTO 변환
        Slice<MessageResponse> responseSlice = messageSlice.map(MessageResponse::success);

        return pageResponseMapper.fromSlice(responseSlice);
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
                binaryContentStorage.put(binaryContent.getId(), attachment.getBytes());

				message.addAttachment(binaryContent);
			}
		}
	}
}
