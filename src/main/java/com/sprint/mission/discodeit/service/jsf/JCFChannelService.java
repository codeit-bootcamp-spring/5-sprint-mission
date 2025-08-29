package com.sprint.mission.discodeit.service.jsf;

import static com.sprint.mission.discodeit.domain.enums.ChannelType.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.sprint.mission.discodeit.domain.dto.ChannelDetail;
import com.sprint.mission.discodeit.domain.dto.CreatePrivateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.CreatePrivateChannelResult;
import com.sprint.mission.discodeit.domain.dto.CreatePublicChannelDTO;
import com.sprint.mission.discodeit.domain.dto.CreatePublicChannelResult;
import com.sprint.mission.discodeit.domain.dto.UpdateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateChannelResult;
import com.sprint.mission.discodeit.domain.entity.Channels;
import com.sprint.mission.discodeit.domain.entity.Messages;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

public class JCFChannelService implements ChannelService {

	private final JCFChannelRepository channelRepository;
	private final MessageService messageService;
	private final MessageRepository messageRepository;

	public JCFChannelService(MessageService messageService, JCFChannelRepository channelRepository,
	  MessageRepository messageRepository) {
		this.messageService = messageService;
		this.channelRepository = channelRepository;
		this.messageRepository = messageRepository;
	}

	@Override
	public CreatePublicChannelResult createPublic(CreatePublicChannelDTO dto) {
		String name = dto.getName();
		String description = dto.getDescription();

		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Channel name cannot be null or empty");
		}
		if (description == null || description.isEmpty()) {
			throw new IllegalArgumentException("Channel description cannot be null or empty");
		}

		Channels savedChannels = channelRepository.save(new Channels(PUBLIC, name, description));

		return CreatePublicChannelResult.builder().channels(savedChannels).build();
	}

	@Override
	public CreatePrivateChannelResult createPrivate(CreatePrivateChannelDTO dto) {

		Channels savedChannels = channelRepository.save(new Channels(PRIVATE));
		return CreatePrivateChannelResult.builder().channels(savedChannels).build();
	}

	@Override
	public List<ChannelDetail> readAllByUserId(UUID userId) {
		List<Channels> channels = channelRepository.findAll().stream().toList();
		List<ChannelDetail> channelDetails = channels.stream()
		  .map(c -> {
			  messageRepository.findAllByChannelId(c.getId());
			  List<Messages> messages = messageRepository.findAllByChannelId(c.getId());
			  Instant lastMessageAt = messages.isEmpty() ? null
				: getLastEditAt(messages);

			  List<UUID> membersIDList = new ArrayList<>();

			  return toReadChannelDetail(c, lastMessageAt, membersIDList);

		  })
		  .toList();
		return channelDetails;
	}

	@Override
	public boolean delete(UUID id) {
		// 연관된 메시지도 삭제
		messageService.deleteAllByChannelId(id);

		channelRepository.delete(id);

		return true;
	}

	@Override
	public UpdateChannelResult update(UpdateChannelDTO dto) {
		String newChannelName = dto.getName();
		String newDescription = dto.getDescription();
		UUID id = dto.getId();

		if (id == null) {
			throw new IllegalArgumentException("Channel ID cannot be null");
		}

		if (newChannelName == null || newChannelName.isEmpty()) {
			throw new IllegalArgumentException("Channel name cannot be null or empty");
		}
		if (newDescription == null || newDescription.isEmpty()) {
			throw new IllegalArgumentException("Channel description cannot be null or empty");
		}

		Channels targetChannels = channelRepository.find(id)
		  .orElseThrow(() -> new NoSuchElementException("Channel with ID " + id + " not found"));

		targetChannels.setName(newChannelName);
		targetChannels.setDescription(newDescription);

		Channels updatedChannels = channelRepository.save(targetChannels);

		return UpdateChannelResult.builder().updatedChannels(updatedChannels).build();
	}

	@Override
	public boolean isEmpty(UUID id) {
		return channelRepository.existsById(id);
	}

	@Override
	public void deleteAll() {
		channelRepository.deleteAll();

		// 연관된 메시지 삭제 (CASCADE DELETE)
		messageService.deleteAll();
	}

	private Instant getLastEditAt(List<Messages> messages) {
		return messages.stream().map(this::getMessageLastEditAt)
		  .max(Instant::compareTo)
		  .orElseThrow(() -> new NoSuchElementException("No messages found"));
	}

	private Instant getMessageLastEditAt(Messages messages) {
		return messages.getUpdatedAt() != null ? messages.getUpdatedAt() : messages.getCreatedAt();
	}

	private ChannelDetail toReadChannelDetail(Channels channels, Instant LastMessageAt,
	  List<UUID> membersIDList) {

		return ChannelDetail.builder()
		  .channels(channels)
		  .lastMessageAt(LastMessageAt)
		  .userIds(membersIDList)
		  .build();
	}
}
