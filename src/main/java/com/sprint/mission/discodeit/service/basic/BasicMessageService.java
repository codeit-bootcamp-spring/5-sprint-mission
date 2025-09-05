package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.CreateBiContentDTO;
import com.sprint.mission.discodeit.domain.dto.CreateMessageDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateMessageDTO;
import com.sprint.mission.discodeit.domain.dto.message.MessageDto;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.MessageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

	private final MessageRepository messageRepository;
	private final UserRepository userRepository;
	private final ChannelRepository channelRepository;
	private final BinaryContentRepository binaryContentRepository;
	private final BasicBinaryContentService binaryContentService;
	private final MessageMapper messageMapper;
	private final UserStatusRepository userStatusRepository;

	@Override
	@Transactional
	public MessageDto create(CreateMessageDTO dto) {
		String content = dto.getContent();
		UUID channelId = dto.getChannelId();
		UUID userId = dto.getUserId();
		List<CreateBiContentDTO> attachmentsInMessage = dto.getAttachments();

		// Validate
		Channel channel = channelRepository.findById(channelId).orElseThrow(() ->
		  new NoSuchElementException("channel with id " + channelId + "not found")
		);

		User user = userRepository.findUserWithProfileImageByID(userId).orElseThrow(() ->
		  new NoSuchElementException("Author with id " + userId + "not found")
		);

		boolean isOnline = userStatusRepository.findByUserId(user.getId())
		  .map(UserStatus::isOnline).orElse(false);

		List<BinaryContent> attachments = Optional.ofNullable(attachmentsInMessage)
		  .map(a -> a.stream().map(binaryContentService::create).toList())
		  .orElse(List.of());

		Message savedMessage = messageRepository.save(new Message(content, user, channel, attachments));

		return messageMapper.toDto(savedMessage, user, isOnline);
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		Message messageToDelete = messageRepository.findById(id)
		  .orElseThrow(() -> new NoSuchElementException("Message with ID " + id + " not found"));

		// 메시지 관련 Attachment 도 삭제
		binaryContentRepository.deleteByIdIn(
		  messageToDelete.getAttachments().stream().map(BinaryContent::getId).toList());

		// 메시지 삭제
		messageRepository.deleteById(id);
	}

	@Override
	@Transactional
	public MessageDto update(UpdateMessageDTO dto) {
		Optional.ofNullable(dto).orElseThrow(() -> new IllegalArgumentException("UpdateMessageDTO cannot be null"));
		UUID id = dto.getId();
		String newContent = dto.getNewContent();

		if (newContent == null || newContent.isEmpty()) {
			throw new IllegalArgumentException("New content cannot be null or empty");
		}

		Message targetMessage = messageRepository.findMessageDetailsById(id).orElseThrow(
		  () -> new NoSuchElementException("Message with ID " + id + " not found"));
		// 1. 내용 수정
		targetMessage.setContent(newContent);

		// 2. User 정보 가져오기
		boolean isOnline = userStatusRepository.findByUserId(targetMessage.getUser().getId())
		  .map(UserStatus::isOnline)
		  .orElse(false);

		messageRepository.save(targetMessage);
		return messageMapper.toDto(targetMessage, targetMessage.getUser(), isOnline);
	}

	@Override
	@Transactional(readOnly = true)
	public MessageDto read(UUID id) {
		Message message = messageRepository.findMessageDetailsById(id)
		  .orElseThrow(() -> new NoSuchElementException("Message with ID " + id + " not found"));

		boolean isOnline = userStatusRepository.findByUserId(message.getUser().getId())
		  .map(UserStatus::isOnline)
		  .orElse(false);

		return messageMapper.toDto(message, message.getUser(), isOnline);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable) {
		Page<Message> messages = messageRepository.findAllDetailsByChannelId(channelId, pageable);

		List<UUID> userIds = messages.map(m -> m.getUser().getId()).stream().toList();

		Map<UUID, Boolean> userId2Status = userStatusRepository.findByUserIdIn(userIds)
		  .stream()
		  .collect(Collectors.toMap(us -> us.getUser().getId(), UserStatus::isOnline));
		return messages.map(message ->
		  messageMapper.toDto(
			message,
			message.getUser(),
			userId2Status.get(message.getUser().getId())
		  )
		);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isEmpty(UUID id) {
		return messageRepository.existsById(id);
	}

}
