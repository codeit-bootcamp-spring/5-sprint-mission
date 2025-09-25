package com.sprint.mission.discodeit.unit.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.util.ReflectionTestUtils;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelUpdateForbiddenException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceUnitTest {

	@Mock
	private ChannelRepository channelRepository;

	@Mock
	private ReadStatusRepository readStatusRepository;

	@Mock
	private MessageRepository messageRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ChannelMapper channelMapper;

	@InjectMocks
	private BasicChannelService channelService;

	@Test
	@DisplayName("채널 생성 - public - 성공")
	public void createPublicChannel_success() {
		PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest("publicChannelName",
			"publicChannelDescription");

		Channel savedChannel = new Channel(ChannelType.PUBLIC, publicChannelCreateRequest.name(),
			publicChannelCreateRequest.description());
		UUID channelId = UUID.randomUUID();

		ChannelDto expectedChannelDto = new ChannelDto(
			channelId,
			ChannelType.PUBLIC,
			publicChannelCreateRequest.name(),
			publicChannelCreateRequest.description(),
			null,
			Instant.now()
		);

		given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);
		given(channelMapper.toDto(any(Channel.class))).willReturn(expectedChannelDto);

		ChannelDto result = channelService.create(publicChannelCreateRequest);

		assertThat(result.name()).isEqualTo(publicChannelCreateRequest.name());
		assertThat(result.description()).isEqualTo(publicChannelCreateRequest.description());
		assertThat(result.type()).isEqualTo(ChannelType.PUBLIC);
		assertThat(result.id()).isEqualTo(channelId);
	}

	@Test
	@DisplayName("채널 생성 - private - 성공")
	public void createPrivateChannel_success() {
		// given
		UUID user1Id = UUID.randomUUID();
		UUID user2Id = UUID.randomUUID();
		List<UUID> participantIds = List.of(user1Id, user2Id);

		PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

		User user1 = new User("user1", "user1@test.com", "password1", null);
		User user2 = new User("user2", "user2@test.com", "password2", null);
		List<User> users = List.of(user1, user2);

		UserDto userDto1 = new UserDto(user1Id, "testUserName1", "testUserEmail1", null, true);
		UserDto userDto2 = new UserDto(user2Id, "testUserName2", "testUserEmail2", null, true);
		List<UserDto> userDtos = List.of(userDto1, userDto2);

		Channel savedChannel = new Channel(ChannelType.PRIVATE, null, null);
		UUID channelId = UUID.randomUUID();
		Instant createdAt = Instant.now();

		ChannelDto expectedChannelDto = new ChannelDto(
			channelId,
			ChannelType.PRIVATE,
			null,
			null,
			userDtos,
			createdAt
		);

		given(userRepository.findAllById(participantIds)).willReturn(users);
		given(channelRepository.save(any(Channel.class))).willReturn(savedChannel);
		given(readStatusRepository.saveAll(anyList())).willReturn(List.of());
		given(channelMapper.toDto(any(Channel.class))).willReturn(expectedChannelDto);

		//
		ChannelDto result = channelService.create(request);

		//
		assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
		assertThat(result.name()).isNull();
		assertThat(result.description()).isNull();
		assertThat(result.participants()).hasSize(2);
		assertThat(result.id()).isEqualTo(channelId);

		then(userRepository).should(times(1)).findAllById(participantIds);
		then(channelMapper).should(times(1)).toDto(any(Channel.class));
	}

	@Test
	@DisplayName("채널 업데이트 - 성공")
	public void updateChannel_success() {
		UUID channelId = UUID.randomUUID();
		PublicChannelUpdateRequest publicChannelUpdateRequest = new PublicChannelUpdateRequest("newChannelName",
			"newChannelDescription");

		Channel exstingChannel = new Channel(ChannelType.PUBLIC, "existingChannelName", "existingChannelDescription");
		given(channelRepository.findById(channelId)).willReturn(Optional.of(exstingChannel));

		ChannelDto channelDto = new ChannelDto(channelId, ChannelType.PUBLIC, "updatedChannelName",
			"updatedChannelDescription", null, Instant.now());

		given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);
		//
		ChannelDto update = channelService.update(channelId, publicChannelUpdateRequest);
		//
		then(channelRepository).should(times(1)).findById(channelId);
		then(channelMapper).should(times(1)).toDto(any(Channel.class));

		assertThat(update.description()).isEqualTo(channelDto.description());
		assertThat(update.name()).isEqualTo(channelDto.name());
	}

	@Test
	@DisplayName("채널 업데이트 - 실패 - 채널 타입이 PRIVATE")
	public void updateChannel_fail() {
		UUID channelId = UUID.randomUUID();
		PublicChannelUpdateRequest publicChannelUpdateRequest = new PublicChannelUpdateRequest("newChannelName",
			"newChannelDescription");
		Channel exstingChannel = new Channel(ChannelType.PRIVATE, "existingChannelName", "existingChannelDescription");

		given(channelRepository.findById(channelId)).willReturn(Optional.of(exstingChannel));

		assertThatThrownBy(() -> channelService.update(channelId, publicChannelUpdateRequest))
			.isInstanceOf(ChannelUpdateForbiddenException.class);

		then(channelRepository).should(times(1)).findById(channelId);
		then(channelRepository).should(never()).save(any(Channel.class));
	}

	@Test
	@DisplayName("채널 삭제 - 성공")
	public void deleteChannel_success() {
		UUID channelId = UUID.randomUUID();
		given(channelRepository.existsById(channelId)).willReturn(true);

		channelService.delete(channelId);

		then(channelRepository).should().existsById(channelId);
		then(messageRepository).should().deleteAllByChannelId(channelId);
		then(readStatusRepository).should().deleteAllByChannelId(channelId);
		then(channelRepository).should().deleteById(channelId);
	}

	@Test
	@DisplayName("채널 삭제 - 실패 - 존재하지 않는 채널")
	public void deleteChannel_fail() {
		UUID uuid = UUID.randomUUID();
		given(channelRepository.existsById(uuid)).willReturn(false);

		assertThatThrownBy(() -> channelService.delete(uuid)).isInstanceOf(ChannelNotFoundException.class);

		then(channelRepository).should().existsById(uuid);
	}

	@Test
	@DisplayName("채널 findAllByUserId - 성공")
	void findAllByUserId_success() {
		UUID userId = UUID.randomUUID();
		UUID privateChannelId = UUID.randomUUID();
		UUID publicChannelId = UUID.randomUUID();

		User user = new User("testUserName", "testUserEmail", "testPassword", null);
		Channel subscribedPrivateChannel = new Channel(ChannelType.PRIVATE, "testPrivateChannel",
			"testPrivateDescription");
		Channel publicChannel = new Channel(ChannelType.PUBLIC, "testPublicChannel", "testPublicDescription");
		ReadStatus readStatus = new ReadStatus(user, subscribedPrivateChannel, Instant.now());

		// 런타임에 강제 주입함
		ReflectionTestUtils.setField(user, "id", userId);
		ReflectionTestUtils.setField(subscribedPrivateChannel, "id", privateChannelId);
		ReflectionTestUtils.setField(publicChannel, "id", publicChannelId);

		UserDto userDto = new UserDto(userId, user.getUsername(), user.getEmail(), null, true);

		ChannelDto subscribedPrivateDto = new ChannelDto(
			privateChannelId,
			ChannelType.PRIVATE,
			subscribedPrivateChannel.getName(),
			subscribedPrivateChannel.getDescription(),
			List.of(userDto),
			Instant.now()
		);
		ChannelDto unSubscribedPublicDto = new ChannelDto(
			publicChannelId,
			ChannelType.PUBLIC,
			publicChannel.getName(),
			publicChannel.getDescription(),
			List.of(userDto),
			Instant.now()
		);

		given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(readStatus));

		List<UUID> expectedSubscribedPrivateIds = List.of(privateChannelId);

		given(channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, expectedSubscribedPrivateIds))
			.willReturn(List.of(publicChannel, subscribedPrivateChannel));
		given(channelMapper.toDto(publicChannel)).willReturn(unSubscribedPublicDto);
		given(channelMapper.toDto(subscribedPrivateChannel)).willReturn(subscribedPrivateDto);

		//
		List<ChannelDto> result = channelService.findAllByUserId(userId);

		//
		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result)
			.extracting(ChannelDto::id)
			.containsExactlyInAnyOrder(publicChannelId, privateChannelId);

		then(readStatusRepository).should(times(1)).findAllByUserId(eq(userId));
		then(channelRepository).should(times(1))
			.findAllByTypeOrIdIn(ChannelType.PUBLIC, expectedSubscribedPrivateIds);
		then(channelMapper).should(times(1)).toDto(eq(publicChannel));
		then(channelMapper).should(times(1)).toDto(eq(subscribedPrivateChannel));
	}

	@Test
	@DisplayName("채널 findAllByUserId - 실패 - 갑자가 DB가 다운될 경우 ")
	public void findAllByUserId_fail() {
		//
		UUID userId = UUID.randomUUID();
		UUID privateId = UUID.randomUUID();

		Channel subscribedPrivate = new Channel(ChannelType.PRIVATE, "subscribePrivateChannel",
			"subscribePrivateChannelDescription");
		User user = new User("usernameTest", "emailTest", "passwordTest", null);
		ReadStatus readStatus = new ReadStatus(user, subscribedPrivate, Instant.now());

		ReflectionTestUtils.setField(subscribedPrivate, "id", privateId);

		given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(readStatus));

		willThrow(new DataAccessResourceFailureException("DB down"))
			.given(channelRepository)
			.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), eq(List.of(privateId)));

		//
		assertThatThrownBy(() -> channelService.findAllByUserId(userId))
			.isInstanceOf(DataAccessResourceFailureException.class);

		//
		then(readStatusRepository).should(times(1)).findAllByUserId(eq(userId));
		then(channelRepository).should(times(1))
			.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of(privateId));
		then(channelMapper).shouldHaveNoInteractions();

	}
}
