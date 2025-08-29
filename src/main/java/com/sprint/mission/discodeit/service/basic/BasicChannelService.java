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
import com.sprint.mission.discodeit.domain.entity.Channels;
import com.sprint.mission.discodeit.domain.entity.Messages;
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

		Channels savedChannels = channelRepository.save(new Channels(PUBLIC, name, description));

		return CreatePublicChannelResult.builder().channels(savedChannels).build();
	}

	@Override
	public CreatePrivateChannelResult createPrivate(CreatePrivateChannelDTO dto) {

		Channels newChannels = new Channels(PRIVATE);

		dto.getUserIds().forEach(
		  id -> readStatusRepository.save(
			new ReadStatus(id, newChannels.getId(), null))
		);

		Channels savedChannels = channelRepository.save(newChannels);

		return CreatePrivateChannelResult.builder().channels(savedChannels).build();
	}

	@Override
	public List<ChannelDetail> readAllByUserId(UUID userId) {
		List<Channels> filteredChannels = channelRepository.findAll().stream()
		  // 필터링: PUBLIC 채널 또는 사용자가 참여한 PRIVATE 채널
		  .filter(c -> c.getType() == PUBLIC ||
			readStatusRepository.findAllByUserId(userId)
			  .stream()
			  .anyMatch(us -> us.getChannel().getId().equals(c.getId())))
		  .toList();

		List<ChannelDetail> channelDetails = filteredChannels.stream()
		  .map(c -> {
			  messageRepository.findAllByChannelId(c.getId());
			  List<Messages> messages = messageRepository.findAllByChannelId(c.getId());
			  Instant lastMessageAt = messages.isEmpty() ? null
				: getLastEditAt(messages);

			  List<UUID> membersIDList = c.getType() == PRIVATE ?
				readStatusRepository.findAllByChannelId(c.getId())
				  .stream()
				  .map((r) -> r.getUser().getId())
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
		Channels targetChannels = channelRepository.find(id)
		  .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));

		if (targetChannels.getType() == PRIVATE) {
			throw new IllegalArgumentException(
			  "Private channel cannot be updated");
		}

		if (newChannelName != null) {
			targetChannels.setName(newChannelName);

		}
		if (newDescription != null) {
			targetChannels.setDescription(newDescription);
		}

		Channels updatedChannels = channelRepository.save(targetChannels);
		return UpdateChannelResult.builder().updatedChannels(updatedChannels).build();
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

	public static CreatePublicChannelResponse toCreatePublicChannelResponse(CreatePublicChannelResult result) {
		return CreatePublicChannelResponse.builder()
		  .id(result.getChannels().getId())
		  .createdAt(result.getChannels().getCreatedAt())
		  .updatedAt(result.getChannels().getUpdatedAt())
		  .type(result.getChannels().getType().toString())
		  .name(result.getChannels().getName())
		  .description(result.getChannels().getDescription())
		  .build();
	}

	public static CreatePrivateChannelResponse toCreatePrivateChannelResponse(CreatePrivateChannelResult result) {
		return CreatePrivateChannelResponse.builder()
		  .id(result.getChannels().getId())
		  .createdAt(result.getChannels().getCreatedAt())
		  .updatedAt(result.getChannels().getUpdatedAt())
		  .type(result.getChannels().getType().toString())
		  .name(result.getChannels().getName())
		  .description(result.getChannels().getDescription())
		  .build();
	}

	public static UpdateChannelResponse toUpdateChannelResponse(UpdateChannelResult result) {
		return UpdateChannelResponse.builder()
		  .id(result.getUpdatedChannels().getId())
		  .createdAt(result.getUpdatedChannels().getCreatedAt())
		  .updatedAt(result.getUpdatedChannels().getUpdatedAt())
		  .type(result.getUpdatedChannels().getType().toString())
		  .name(result.getUpdatedChannels().getName())
		  .description(result.getUpdatedChannels().getDescription())
		  .build();
	}

	public static ReadChannelResponse channelDetailsToReadChannelResponse(ChannelDetail channelDetail) {
		return ReadChannelResponse.builder()
		  .id(channelDetail.getChannels().getId())
		  .type(channelDetail.getChannels().getType())
		  .name(channelDetail.getChannels().getName())
		  .description(channelDetail.getChannels().getDescription())
		  .participantIds(channelDetail.getUserIds())
		  .lastMessageAt(channelDetail.getLastMessageAt())
		  .build();
	}

}
