package com.sprint.mission.discodeit.service.basic;

import static com.sprint.mission.discodeit.domain.enums.ChannelType.*;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.domain.dto.CreatePrivateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.CreatePublicChannelDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateChannelDTO;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

	private final ChannelRepository channelRepository;
	private final MessageRepository messageRepository;
	private final ReadStatusRepository readStatusRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public Channel createPublic(CreatePublicChannelDTO dto) {
		String name = dto.getName();
		String description = dto.getDescription();

		return channelRepository.save(new Channel(PUBLIC, name, description));
	}

	@Override
	@Transactional
	public Channel createPrivate(CreatePrivateChannelDTO dto) {

		Channel newChannel = new Channel(PRIVATE);
		channelRepository.save(newChannel);
		dto.getUserIds().forEach(
		  id -> readStatusRepository.save(
			new ReadStatus(
			  userRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("userId with" + id + "notFound")),
			  newChannel
			))
		);

		return newChannel;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Channel> readAllByUserId(UUID userId) {
		// 필터링: PUBLIC 채널 또는 사용자가 참여한 PRIVATE 채널
		List<Channel> filteredChannels = channelRepository.findAll().stream()
		  .filter(c -> c.getType() == PUBLIC || readStatusRepository.findAllByUserId(userId)
			.stream()
			.anyMatch(us -> us.getChannel().getId().equals(c.getId())))
		  .toList();

		return filteredChannels;
	}

	@Override
	@Transactional
	public boolean delete(UUID id) {
		if (!channelRepository.existsById(id)) {
			throw new NoSuchElementException("Channel with id " + id + " not found");
		}
		// 연관된 메시지도 삭제
		messageRepository.deleteByChannelId(id);
		// 연관된 유저 상태도 삭제
		readStatusRepository.deleteByChannelId(id);

		channelRepository.deleteById(id);

		return true;
	}

	@Override
	@Transactional
	public Channel update(UpdateChannelDTO dto) {
		UUID id = dto.getId();
		String newChannelName = dto.getName();
		String newDescription = dto.getDescription();

		// 채널이 존재하는지 확인
		Channel targetChannel = channelRepository.findById(id)
		  .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));

		if (targetChannel.getType() == PRIVATE) {
			throw new IllegalArgumentException(
			  "Private channel cannot be updated");
		}

		if (newChannelName != null) {
			targetChannel.setName(newChannelName);

		}
		if (newDescription != null) {
			targetChannel.setDescription(newDescription);
		}

		return channelRepository.save(targetChannel);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isEmpty(UUID id) {
		return !channelRepository.existsById(id);
	}

	@Override
	@Transactional
	public void deleteAll() {
		channelRepository.deleteAll();
		messageRepository.deleteAll();
	}

	@Override
	public List<User> getChannelParticipants(UUID id) {
		Channel channel = channelRepository.findById(id)
		  .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));

		if (channel.getType() == PUBLIC) {
			throw new UnsupportedOperationException("Public Channel not have participants");
		}

		return readStatusRepository.findAllByChannelId(id).stream().map(ReadStatus::getUser).toList();
	}

	private Instant getLastEditAt(Page<Message> messages) {
		return messages.stream().map(this::getMessageLastEditAt)
		  .max(Instant::compareTo)
		  .orElseThrow(() -> new NoSuchElementException("No messages found"));
	}

	private Instant getMessageLastEditAt(Message message) {
		return message.getUpdatedAt() != null ? message.getUpdatedAt() : message.getCreatedAt();
	}

}
