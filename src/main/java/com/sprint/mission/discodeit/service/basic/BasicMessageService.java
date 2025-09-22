package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessagesGetByAuthorRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageDeleteResponse;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.dto.response.page.PageOffsetResponse;
import com.sprint.mission.discodeit.dto.response.page.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
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
        log.info("[Service] 메시지 생성 시도");
        log.debug("[Service] 메시지 생성 요청 데이터 : {}", request);
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getAuthorId()));

		Channel channel = channelRepository.findById(request.getChannelId())
				.orElseThrow(ChannelNotFoundException::new);

		Message message = new Message(author, channel, request.getContent());

		addAttachments(message, request.getAttachments());
        messageRepository.save(message);

        log.info("[Service] 메시지 생성 성공");
        log.debug("[Service] 메시지 생성 완료 데이터 : {}", message);
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
    public PageOffsetResponse<MessageResponse> findPageMessagesByChannel(UUID channelId, int page, int size, String[] sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Message> messagePage = messageRepository.findPageByChannelId(channelId, pageable);
        Page<MessageResponse> responsePage = messagePage.map(MessageResponse::success);
        return pageResponseMapper.fromPageToOffset(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageOffsetResponse<MessageResponse> findSliceMessagesByChannel(UUID channelId, int page, int size, String[] sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Slice<Message> messageSlice = messageRepository.findSliceByChannelId(channelId, pageable);

        Slice<MessageResponse> responseSlice = messageSlice.map(MessageResponse::success);

        return pageResponseMapper.fromSliceToOffset(responseSlice);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageResponse> findMessagesByChannelWithCursor(UUID channelId, Instant cursor, int size, String sort) {
        Sort sortObj = createSort(sort);
        Pageable pageable = PageRequest.of(0, size, sortObj);

        if (cursor == null) {
            cursor = Instant.parse("9999-12-31T23:59:59Z");
        }
        Slice<Message> messageSlice = messageRepository.findByChannelIdWithCursor(channelId, cursor, pageable);

        Object nextCursor = null;
        if (messageSlice.hasNext() && !messageSlice.getContent().isEmpty()) {
            Message lastMessage = messageSlice.getContent().get(messageSlice.getContent().size() - 1);
            nextCursor = lastMessage.getCreatedAt();
        }

        return pageResponseMapper.fromSliceToCursor(messageSlice.map(MessageResponse::success), nextCursor);
    }

	@Override
    @Transactional
	public MessageResponse updateMessage(UUID messageId, MessageUpdateRequest request) {
        log.info("[Service] 메시지 수정 시도");
        log.debug("[Service] 메시지 수정 요청 데이터 : {}", request);
		Message message = messageRepository.findById(messageId)
				.orElseThrow(MessageNotFoundException::new);

		if (!message.getAuthor().getId().equals(request.getAuthorId())) {
            log.warn("[Service] 메시지 수정 권한 없음 - messageId: {}, authorId: {}", messageId, request.getAuthorId());
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

        log.info("[Service] 메시지 수정 성공");
        log.debug("[Service] 메시지 수정 완료 데이터 : {}", message);
		return MessageResponse.success(message);
	}

	@Override
    @Transactional
	public MessageDeleteResponse deleteMessage(UUID messageId, UUID authorId) {
        log.info("[Service] 메시지 삭제 시도");
        log.debug("[Service] 메시지 삭제 요청 데이터 - messageId: {}, authorId: {}", messageId, authorId);
		Message message = messageRepository.findById(messageId)
			.orElseThrow(MessageNotFoundException::new);

		if (!message.getAuthor().getId().equals(authorId)) {
            log.warn("[Service] 메시지 삭제 권한 없음 - messageId: {}, authorId: {}", messageId, authorId);
			throw new UnauthorizedMessageAccessException();
		}

        for (BinaryContent attachment : message.getAttachments()) {
            binaryContentRepository.deleteById(attachment.getId());
        }

		messageRepository.deleteById(messageId);

        log.info("[Service] 메시지 삭제 성공");
        log.debug("[Service] 메시지 삭제 완료 데이터 - messageId: {}, authorId: {}", messageId, authorId);
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
				BinaryContent bc = binaryContentRepository.save(binaryContent);
                message.addAttachment(bc);
                binaryContentStorage.put(binaryContent.getId(), attachment.getBytes());
			}
		}
	}

    private Sort createSort(String sort) {
        String[] parts = sort.split(",");
        String property = parts[0];
        Sort.Direction direction = (parts.length > 1 && "desc".equals(parts[1]))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return Sort.by(direction, property);
    }
}
