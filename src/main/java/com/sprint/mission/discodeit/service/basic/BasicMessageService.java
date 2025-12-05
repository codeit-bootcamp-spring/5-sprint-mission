package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.dto.PageResponse;
import com.sprint.mission.discodeit.dto.message.MessageCreateCommand;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.log.LogUtils;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("messageService")
@RequiredArgsConstructor
@Slf4j
public class BasicMessageService implements MessageService {

	private final MessageRepository messageRepository;
	private final ChannelRepository channelRepository;
	private final UserRepository userRepository;
	private final BinaryContentRepository binaryContentRepository;
	private final MessageMapper messageMapper;
	private final PageResponseMapper pageResponseMapper;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	@Transactional
	public MessageDto create(MessageCreateCommand command) {
		log.debug("[BasicMessageService#create] try command={}", command.forLog());

		String content = command.content();
		User author = userRepository.findById(command.authorId())
			.orElseThrow(() -> new UserNotFoundException().addDetail("author", command.authorId()));
		Channel channel = channelRepository.findById(command.channelId())
			.orElseThrow(
				() -> new ChannelNotFoundException().addDetail("channel", command.channelId()));

		List<BinaryContent> attachments = command.attachments().stream()
			.map(request -> {
				BinaryContent binaryContent = new BinaryContent(
					request.fileName(),
					request.contentType(),
					request.bytes().length
				);
				binaryContentRepository.save(binaryContent);
				eventPublisher.publishEvent(new BinaryContentCreatedEvent(binaryContent.getId(), request.bytes()));
				log.debug(
					"[BasicMessageService#create] BinaryContent created: filename={}, contentType={}, size={}",
					binaryContent.getFileName(),
					binaryContent.getContentType(),
					LogUtils.humanReadableSize(binaryContent.getSize()));
				return binaryContent;
			})
			.toList();

		Message message = new Message(content, channel, author, attachments);

		MessageDto dto = messageMapper.toDto(messageRepository.save(message));
		log.info("[MessageService#create] Message Created:{}", dto);
		eventPublisher.publishEvent(new MessageCreatedEvent(
			message.getContent(),
			channel.getId(),
			channel.getName(),
			author.getId(),
			author.getUsername()
		));

		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public MessageDto findById(UUID messageId) {
		return messageMapper.toDto(validateId(messageId));
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,
		Pageable pageable) {

		if (!channelRepository.existsById(channelId)) {
			throw new ChannelNotFoundException().addDetail("channel", channelId);
		}

		Slice<MessageDto> slice = messageRepository.search(channelId, cursor, pageable)
			.map(messageMapper::toDto);

		Instant nextCursor = slice.hasNext()
			? slice.getContent().get(slice.getContent().size() - 1).createdAt()
			: null;

		return pageResponseMapper.fromSlice(slice, nextCursor);
	}

	@Override
	@Transactional
	@PreAuthorize("@messageService.isOwner(#messageId, principal.userDto.id)")
	public MessageDto update(UUID messageId, MessageUpdateRequest request) {
		log.debug("[BasicMessageService#update] try messageId={} request={}", messageId, request);
		Message message = validateId(messageId);
		message.update(request.newContent());

		MessageDto dto = messageMapper.toDto(messageRepository.save(message));
		log.info("[MessageService#update] Message Updated:{}", dto);

		return dto;
	}

	@Override
	@Transactional
	@PreAuthorize("@messageService.isOwner(#messageId, principal.userDto.id)")
	public void delete(UUID messageId) {
		log.debug("[BasicMessageService#delete] try messageId={}", messageId);
		Message message = validateId(messageId);

		if (message.getAttachments() != null && !message.getAttachments().isEmpty()) {
			for (BinaryContent binaryContent : message.getAttachments()) {
				binaryContentRepository.deleteById(binaryContent.getId());
				log.debug("[MessageService#delete] BinaryContent Deleted:{}", binaryContent.getId());
			}
		}

		messageRepository.deleteById(message.getId());
		log.info("[MessageService#delete] Message Deleted:{}", message.getId());
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isOwner(UUID messageId, UUID userId) {
		return messageRepository.findById(messageId)
			.map(Message::getAuthor)
			.map(User::getId)
			.filter(userId::equals)
			.isPresent();
	}

	private Message validateId(UUID messageId) {
		return messageRepository.findById(messageId)
			.orElseThrow(() -> new MessageNotFoundException().addDetail("id", messageId));
	}
}
