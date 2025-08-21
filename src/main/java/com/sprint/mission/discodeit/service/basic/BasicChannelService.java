package com.sprint.mission.discodeit.service.basic;

import static com.sprint.mission.discodeit.domain.enums.ChannelType.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.domain.dto.ChannelDetail;
import com.sprint.mission.discodeit.domain.dto.CreatePrivateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.CreatePrivateChannelResult;
import com.sprint.mission.discodeit.domain.dto.CreatePublicChannelDTO;
import com.sprint.mission.discodeit.domain.dto.CreatePublicChannelResult;
import com.sprint.mission.discodeit.domain.dto.UpdateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateChannelResult;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.domain.response.CreatePrivateChannelResponse;
import com.sprint.mission.discodeit.domain.response.CreatePublicChannelResponse;
import com.sprint.mission.discodeit.domain.response.ReadChannelResponse;
import com.sprint.mission.discodeit.domain.response.UpdateChannelResponse;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

	private final ChannelRepository channelRepository;
	private final MessageRepository messageRepository;
	private final ReadStatusRepository readStatusRepository;

	@Override
	public CreatePublicChannelResult createPublic(CreatePublicChannelDTO dto) {
		String name = dto.getName();
		String description = dto.getDescription();

		Channel savedChannel = channelRepository.save(new Channel(PUBLIC, name, description));

		return CreatePublicChannelResult.builder().channel(savedChannel).build();
	}

	@Override
	public CreatePrivateChannelResult createPrivate(CreatePrivateChannelDTO dto) {

		Channel newChannel = new Channel(PRIVATE);

		dto.getUserIds().forEach(
		  id -> readStatusRepository.save(
			new ReadStatus(id, newChannel.getId(), null))
		);

		Channel savedChannel = channelRepository.save(newChannel);

		return CreatePrivateChannelResult.builder().channel(savedChannel).build();
	}

	@Override
	public List<ChannelDetail> readAllByUserId(UUID userId) {
		List<Channel> filteredChannels = channelRepository.findAll().stream()
		  // 필터링: PUBLIC 채널 또는 사용자가 참여한 PRIVATE 채널
		  .filter(c -> c.getChannelType() == PUBLIC ||
			readStatusRepository.findAllByUserId(userId)
			  .stream()
			  .anyMatch(us -> us.getChannelId().equals(c.getId())))
		  .toList();

		List<ChannelDetail> channelDetails = filteredChannels.stream()
		  .map(c -> {
			  messageRepository.findAllByChannelId(c.getId());
			  List<Message> messages = messageRepository.findAllByChannelId(c.getId());
			  Instant lastMessageAt = messages.isEmpty() ? null
				: getLastEditAt(messages);

			  List<UUID> membersIDList = c.getChannelType() == PRIVATE ?
				readStatusRepository.findAllByChannelId(c.getId())
				  .stream()
				  .map(ReadStatus::getUserId)
				  .toList()
				: new ArrayList<>();

			  return toReadChannelDetail(c, lastMessageAt, membersIDList);

		  })
		  .toList();

		return channelDetails;

	}

	@Override
	public boolean delete(UUID id) {
		if (!channelRepository.existsById(id)) {
			throw new NoSuchElementException("Channel with id " + id + " not found");
		}
		// 연관된 메시지도 삭제
		messageRepository.deleteByChannelId(id);
		// 연관된 유저 상태도 삭제
		readStatusRepository.deleteByChannelId(id);

		channelRepository.delete(id);

		return true;
	}

	@Override
	public UpdateChannelResult update(UpdateChannelDTO dto) {
		UUID id = dto.getId();
		String newChannelName = dto.getName();
		String newDescription = dto.getDescription();

		// 채널이 존재하는지 확인
		Channel targetChannel = channelRepository.find(id)
		  .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));

		if (targetChannel.getChannelType() == PRIVATE) {
			throw new IllegalArgumentException(
			  "Private channel cannot be updated");
		}

		if (newChannelName != null) {
			targetChannel.setName(newChannelName);

		}
		if (newDescription != null) {
			targetChannel.setDescription(newDescription);
		}

		Channel updatedChannel = channelRepository.save(targetChannel);
		return UpdateChannelResult.builder().updatedChannel(updatedChannel).build();
	}

	@Override
	public boolean isEmpty(UUID id) {
		return !channelRepository.existsById(id);
	}

	@Override
	public void deleteAll() {
		channelRepository.deleteAll();
		messageRepository.deleteAll();
	}

	private Instant getLastEditAt(List<Message> messages) {
		return messages.stream().map(this::getMessageLastEditAt)
		  .max(Instant::compareTo)
		  .orElseThrow(() -> new NoSuchElementException("No messages found"));
	}

	private Instant getMessageLastEditAt(Message message) {
		return message.getUpdatedAt() != null ? message.getUpdatedAt() : message.getCreatedAt();
	}

	private ChannelDetail toReadChannelDetail(Channel channel, Instant LastMessageAt,
	  List<UUID> membersIDList) {

		return ChannelDetail.builder()
		  .channel(channel)
		  .lastMessageAt(LastMessageAt)
		  .userIds(membersIDList)
		  .build();
	}

	public static CreatePublicChannelResponse toCreatePublicChannelResponse(CreatePublicChannelResult result) {
		return CreatePublicChannelResponse.builder()
		  .id(result.getChannel().getId())
		  .createdAt(result.getChannel().getCreatedAt())
		  .updatedAt(result.getChannel().getUpdatedAt())
		  .type(result.getChannel().getChannelType().toString())
		  .name(result.getChannel().getName())
		  .description(result.getChannel().getDescription())
		  .build();
	}

	public static CreatePrivateChannelResponse toCreatePrivateChannelResponse(CreatePrivateChannelResult result) {
		return CreatePrivateChannelResponse.builder()
		  .id(result.getChannel().getId())
		  .createdAt(result.getChannel().getCreatedAt())
		  .updatedAt(result.getChannel().getUpdatedAt())
		  .type(result.getChannel().getChannelType().toString())
		  .name(result.getChannel().getName())
		  .description(result.getChannel().getDescription())
		  .build();
	}

	public static UpdateChannelResponse toUpdateChannelResponse(UpdateChannelResult result) {
		return UpdateChannelResponse.builder()
		  .id(result.getUpdatedChannel().getId())
		  .createdAt(result.getUpdatedChannel().getCreatedAt())
		  .updatedAt(result.getUpdatedChannel().getUpdatedAt())
		  .type(result.getUpdatedChannel().getChannelType().toString())
		  .name(result.getUpdatedChannel().getName())
		  .description(result.getUpdatedChannel().getDescription())
		  .build();
	}

	public static ReadChannelResponse channelDetailsToReadChannelResponse(ChannelDetail channelDetail) {
		return ReadChannelResponse.builder()
		  .id(channelDetail.getChannel().getId())
		  .type(channelDetail.getChannel().getChannelType())
		  .name(channelDetail.getChannel().getName())
		  .description(channelDetail.getChannel().getDescription())
		  .participantIds(channelDetail.getUserIds())
		  .lastMessageAt(channelDetail.getLastMessageAt())
		  .build();
	}

}
