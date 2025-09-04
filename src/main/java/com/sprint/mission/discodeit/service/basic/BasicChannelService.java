package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.channel.ChannelLeaveRequest;
import com.sprint.mission.discodeit.dto.request.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.channel.ChannelCreateResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelDeleteResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelLeaveResponse;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.DuplicateChannelNameException;
import com.sprint.mission.discodeit.exception.channel.NotChannelMemberException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
	private final ChannelRepository channelRepository;
	private final ReadStatusRepository readStatusRepository;
	private final MessageRepository messageRepository;
	private final UserRepository  userRepository;


	@Override
    @Transactional
	public ChannelCreateResponse create(PublicChannelCreateRequest request) {
		if (channelRepository.existsByName(request.getName())) {
			throw new DuplicateChannelNameException();
		}

		Channel channel = new Channel(request.getName(), request.getDescription());
		channelRepository.save(channel);

		List<User> allUsers = userRepository.findAll();
		for (User user : allUsers) {
			ReadStatus readStatus = new ReadStatus(user, channel);
			readStatusRepository.save(readStatus);
		}

		return ChannelCreateResponse.success(channel);
	}

	@Override
    @Transactional
	public ChannelCreateResponse create(PrivateChannelCreateRequest request) {

		List<java.util.UUID> participantIds = request.getParticipantIds().stream().distinct().toList();

		for (java.util.UUID userId : participantIds) {
			if (userId == null) {
				throw new IllegalArgumentException("참여자 ID는 null일 수 없습니다.");
			}
			if (!userRepository.existsById(userId)) {
				throw new UserNotFoundException();
			}
		}

		Channel channel = new Channel(participantIds);
		channelRepository.save(channel);

		for (UUID userId : participantIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(UserNotFoundException::new);

			ReadStatus readStatus = new ReadStatus(user, channel);
			readStatusRepository.save(readStatus);
		}

		return ChannelCreateResponse.successWithMembers(channel, participantIds);
	}

	@Override
    @Transactional(readOnly = true)
	public ChannelResponse findByName(String channelName) {
		Channel channel = channelRepository.findByName(channelName)
			.orElseThrow(ChannelNotFoundException::new);

		return createChannelByType(channel);
	}

	@Override
    @Transactional(readOnly = true)
	public ChannelResponse find(UUID channelId) {
		Channel channel = channelRepository.findById(channelId)
			.orElseThrow(ChannelNotFoundException::new);

		return createChannelByType(channel);
	}

	@Override
    @Transactional(readOnly = true)
	public List<ChannelResponse> findChannelsByUserId(UUID userId) {

		List<UUID> myChannelIds = readStatusRepository.findByUserId(userId).stream()
				.map(rs -> rs.getChannel().getId())
				.toList();

		return channelRepository.findAll().stream()
				.filter(channel -> myChannelIds.contains(channel.getId()))
				.map(this::createChannelByType)
				.toList();
	}


	// updateChannel 메서드
	@Override
    @Transactional
	public ChannelResponse updateChannel(UUID channelId, ChannelUpdateRequest request) {
		Channel channel = channelRepository.findById(channelId)
			.orElseThrow(ChannelNotFoundException::new);

		if (channel.getType().equals(ChannelType.PRIVATE)) {
			throw new PrivateChannelUpdateException();
		}

		if (request.getNewName() != null &&channelRepository.existsByName(request.getNewName())) {
			throw new DuplicateChannelNameException();
		}

		if (request.getNewName() != null) {
			channel.setName(request.getNewName());
		}
		if (request.getNewDescription() != null) {
			channel.setDescription(request.getNewDescription());
		}
		channelRepository.save(channel);

		return createChannelByType(channel);
	}

	@Override
    @Transactional
	public ChannelLeaveResponse leaveChannel(ChannelLeaveRequest request) {
		Channel channel = channelRepository.findById(request.getChannelId())
				.orElseThrow(ChannelNotFoundException::new);

		ReadStatus readStatus = readStatusRepository.findByChannelIdAndUserId(
				request.getChannelId(), request.getUserId());

		if (readStatus == null) {
			throw new NotChannelMemberException();
		}

		readStatusRepository.deleteById(readStatus.getId());

		channelRepository.save(channel);

		String nickname = userRepository.findById(request.getUserId())
				.map(User::getDefaultNickname)
				.orElseThrow(UserNotFoundException::new);

		return ChannelLeaveResponse.success(channel, request.getUserId(), nickname);
	}

	@Override
    @Transactional
	public ChannelDeleteResponse deleteChannel(UUID channelId) {
		// userId는 나중에 admin 혹은 권한 체크를 위해서 남겨둠

		Channel channel = channelRepository.findById(channelId)
			.orElseThrow(ChannelNotFoundException::new);
		channelRepository.deleteById(channelId);
		return ChannelDeleteResponse.success(channel);
	}

	private ChannelResponse createChannelByType(Channel channel) {
		Instant lastMessageTime = getLastMessageTime(channel.getId());

		if (ChannelType.PRIVATE.equals(channel.getType())) {
            List<UUID> participantIds = getPrivateChannelParticipants(channel.getId());

            List<UserResponse> participants = participantIds.stream()
                    .map(userId -> userRepository.findById(userId).orElse(null))
                    .filter(Objects::nonNull)
                    .map(UserResponse::success)
                    .toList();
			return ChannelResponse.fromPrivateChannel(channel, lastMessageTime, participants);
		} else {
			return ChannelResponse.fromPublicChannel(channel, lastMessageTime);
		}
	}

	private Instant getLastMessageTime(UUID channelId) {
		List<Message> messages = messageRepository.findByChannelId(channelId);
		return messages.stream()
			.map(Message::getCreatedAt)
			.max(Instant::compareTo)
			.orElse(null);
	}

	private List<UUID> getPrivateChannelParticipants(UUID channelId) {
		return readStatusRepository.findByChannelId(channelId)
			.stream()
			.map(rs -> rs.getUser().getId())
			.toList();
	}
}
