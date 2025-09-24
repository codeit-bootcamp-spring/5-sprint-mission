package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.AuthorNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.server.InternalServerErrorException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.MessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicMessageService implements MessageService {

	private final MessageRepository messageRepository;
	private final UserRepository userRepository;
	private final ChannelRepository channelRepository;
	private final BinaryContentRepository binaryContentRepository;
	private final BasicBinaryContentService binaryContentService;
	private final MessageMapper messageMapper;
	private final UserMapper userMapper;
	private final UserStatusRepository userStatusRepository;
	private final BinaryContentMapper binaryContentMapper;

	@Override
	@Transactional
	public MessageDto create(CreateMessageDTO dto) {
		log.debug("PUBLIC channel create 트랜잭션 시작");

		String content = dto.getContent();
		UUID channelId = dto.getChannelId();
		UUID userId = dto.getUserId();
		List<CreateBiContentDTO> attachmentsInMessage = dto.getAttachments();

		// Validate
		Channel channel = channelRepository.findById(channelId).orElseThrow(ChannelNotFoundException::new);

		User user = userRepository.findUserWithProfileImageByID(userId).orElseThrow(AuthorNotFoundException::new);

		boolean isOnline = userStatusRepository.findByUserId(user.getId())
		  .map(UserStatus::isOnline).orElse(false);

		List<BinaryContent> attachments = Optional.ofNullable(attachmentsInMessage)
		  .map(a -> a.stream().map(binaryContentService::create).toList())
		  .orElse(List.of());
		log.debug("success save attachments with Ids={} in message"
		  , attachments.stream().map(BinaryContent::getId).toString());

		Message savedMessage = messageRepository.save(new Message(content, user, channel, attachments));
		log.debug("success save messageEntity  ID={}", savedMessage.getId());

		log.debug("Message create 트랜잭션 정상 종료");
		return messageMapper.toDto(
		  savedMessage,
		  userMapper.toDto(user, isOnline, binaryContentMapper.toDto(user.getProfileImage())));
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		log.debug("message  delete 트랜잭션 시작");

		Message messageToDelete = messageRepository.findById(id).orElseThrow(MessageNotFoundException::new);

		// 메시지 관련 Attachment 도 삭제
		binaryContentRepository.deleteByIdIn(
		  messageToDelete.getAttachments().stream().map(BinaryContent::getId).toList());
		log.debug("success delete attachments with Ids={} in message",
		  messageToDelete.getAttachments().stream().map(BinaryContent::getId).toString());

		// 메시지 삭제
		messageRepository.deleteById(id);
		log.debug("success delete messageEntity id={}", id);

		log.debug("message  delete 트랜잭션 정상 종료");
	}

	@Override
	@Transactional
	public MessageDto update(UpdateMessageDTO dto) {
		log.debug("message update 트랜잭션 시작");

		UUID id = dto.getId();
		String newContent = dto.getNewContent();
		log.debug("new message info id={} newContent={}", id, newContent);

		if (newContent == null || newContent.isEmpty()) {
			log.error("bad request: New content cannot be null or empty");
			throw new InternalServerErrorException(Map.of("detail", "New content cannot be null or empty"));
		}

		Message targetMessage = messageRepository.findMessageDetailsById(id).orElseThrow(MessageNotFoundException::new);
		// 1. 내용 수정
		targetMessage.setContent(newContent);

		// 2. User 정보 가져오기
		User user = targetMessage.getUser();
		boolean isOnline = userStatusRepository.findByUserId(targetMessage.getUser().getId())
		  .map(UserStatus::isOnline)
		  .orElse(false);

		messageRepository.save(targetMessage);
		log.debug("success update messageEntity  channelId={}", id);

		log.debug("message update 트랜잭션 정상 종료");
		return messageMapper.toDto(
		  targetMessage,
		  userMapper.toDto(user, isOnline, binaryContentMapper.toDto(user.getProfileImage())));
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
		  {
			  User user = message.getUser();
			  boolean isOnline = userId2Status.get(user.getId());
			  return messageMapper.toDto(
				message,
				userMapper.toDto(user, isOnline, binaryContentMapper.toDto(user.getProfileImage()))
			  );
		  }
		);
	}

	@Override
	@Transactional(readOnly = true)
	public Slice<MessageDto> findAllCursorByChannelId(UUID channelId, Instant cursor, Pageable pageable) {
		Slice<Message> messages = messageRepository.findAllDetailsByChannelIdAndCursor(channelId, cursor, pageable);

		List<UUID> userIds = messages.map(m -> m.getUser().getId()).stream().toList();

		Map<UUID, Boolean> userId2Status = userStatusRepository.findByUserIdIn(userIds)
		  .stream()
		  .collect(Collectors.toMap(us -> us.getUser().getId(), UserStatus::isOnline));
		return messages.map(message ->
		  {
			  User user = message.getUser();
			  boolean isOnline = userId2Status.get(user.getId());
			  return messageMapper.toDto(
				message,
				userMapper.toDto(user, isOnline, binaryContentMapper.toDto(user.getProfileImage()))
			  );
		  }
		);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isEmpty(UUID id) {
		return messageRepository.existsById(id);
	}

}
