package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.any;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceTest {

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

	private PublicChannelCreateRequest publicRequest;
	private PrivateChannelCreateRequest privateRequest;
	private PublicChannelUpdateRequest updateRequest;
	private ChannelDto channelDto;
	private Channel publicChannel;
	private Channel privateChannel;
	private User user;
	private UserDto userDto;
	private ReadStatus readStatus;
	private List<User> participants;
	private UUID channelId;

	@BeforeEach
	public void setUp() {
		String name = "test name";
		String description = "test description";
		UUID userId = UUID.randomUUID();
		channelId = UUID.randomUUID();
		user = new User("test",
			"test@email.com",
			"12341234",
			null);
		ReflectionTestUtils.setField(user, "id", userId);
		publicRequest = new PublicChannelCreateRequest(
			name,
			description
		);
		privateRequest = new PrivateChannelCreateRequest(
			List.of(userId)
		);
		publicChannel = new Channel(
			ChannelType.PUBLIC,
			name,
			description
		);
		ReflectionTestUtils.setField(publicChannel, "id", channelId);
		privateChannel = new Channel(
			ChannelType.PRIVATE,
			null,
			null
		);
		ReflectionTestUtils.setField(privateChannel, "id", channelId);
		ReflectionTestUtils.setField(privateChannel, "createdAt", Instant.now());

		userDto = UserDto.builder()
			.id(user.getId())
			.username(user.getUsername())
			.email(user.getEmail())
			.profile(null)
			.online(false)
			.build();

		participants = List.of(user);
		readStatus = new ReadStatus(privateChannel.getCreatedAt(), user, privateChannel);
	}

	@Test
	@DisplayName("public 채널 생성 테스트")
	void createPublicChannel() {
		channelDto = new ChannelDto(
			publicChannel.getId(),
			publicChannel.getType(),
			publicChannel.getName(),
			publicChannel.getDescription(),
			null,
			null
		);

		given(channelRepository.save(any())).willReturn(publicChannel);
		given(messageRepository.findTopByChannelIdOrderByCreatedAtDescIdDesc(any()))
			.willReturn(Optional.empty());
		given(readStatusRepository.findAllByChannelId(any())).willReturn(List.of());
		given(userRepository.findAllByIdIn(any())).willReturn(List.of());
		given(channelMapper.toDto(any(), any(), any())).willReturn(channelDto);

		ChannelDto result = channelService.create(publicRequest);

		assertThat(result).isEqualTo(channelDto);
		verify(channelRepository, times(1)).save(any());
		verify(messageRepository, times(1)).findTopByChannelIdOrderByCreatedAtDescIdDesc(any());
		verify(readStatusRepository, times(1)).findAllByChannelId(any());
		verify(userRepository, times(1)).findAllByIdIn(any());
		verify(channelMapper, times(1)).toDto(any(), any(), any());
	}

	@Test
	@DisplayName("private 채널 생성 테스트")
	void createPrivateChannel() {
		channelDto = new ChannelDto(
			privateChannel.getId(),
			ChannelType.PRIVATE,
			null,
			null,
			List.of(userDto),
			null
		);

		given(userRepository.findById(any())).willReturn(Optional.of(user));
		given(channelRepository.save(any())).willReturn(privateChannel);
		for (User user : participants) {
			given(readStatusRepository.save(any())).willReturn(readStatus);
		}
		given(messageRepository.findTopByChannelIdOrderByCreatedAtDescIdDesc(any()))
			.willReturn(Optional.empty());
		given(readStatusRepository.findAllByChannelId(any())).willReturn(List.of(readStatus));
		given(userRepository.findAllByIdIn(any())).willReturn(List.of(user));
		given(channelMapper.toDto(any(), any(), any())).willReturn(channelDto);

		ChannelDto result = channelService.create(privateRequest);

		assertThat(result).isEqualTo(channelDto);
		verify(userRepository, times(1)).findById(any());
		verify(channelRepository, times(1)).save(any());
		verify(readStatusRepository, times(1)).save(any());
		verify(readStatusRepository, times(1)).findAllByChannelId(any());
		verify(messageRepository, times(1)).findTopByChannelIdOrderByCreatedAtDescIdDesc(any());
		verify(readStatusRepository, times(1)).findAllByChannelId(any());
		verify(userRepository, times(1)).findAllByIdIn(any());
		verify(channelMapper, times(1)).toDto(any(), any(), any());
	}

	@Test
	@DisplayName("private 채널 생성 실패(존재하지 않는 유저)")
	void createPrivateChannelFailureWithUserNotFound() {
		privateRequest = new PrivateChannelCreateRequest(List.of(UUID.randomUUID()));
		given(userRepository.findById(any())).willReturn(Optional.empty());
		assertThatThrownBy(() -> channelService.create(privateRequest))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@DisplayName("public 채널 업데이트")
	void updatePublicChannel() {
		given(channelRepository.findById(any())).willReturn(Optional.of(publicChannel));

		updateRequest = new PublicChannelUpdateRequest(
			"update name", "update description");
		publicChannel = new Channel(ChannelType.PUBLIC, "update name", "update description");
		channelDto = new ChannelDto(
			publicChannel.getId(),
			publicChannel.getType(),
			publicChannel.getName(),
			publicChannel.getDescription(),
			null,
			null
		);

		given(channelRepository.save(any())).willReturn(publicChannel);
		given(channelMapper.toDto(any(), any(), any())).willReturn(channelDto);

		ChannelDto result = channelService.update(channelId, updateRequest);

		assertThat(result).isEqualTo(channelDto);
		verify(channelRepository, times(1)).save(any());
		verify(channelMapper, times(1)).toDto(any(), any(), any());
		verify(channelRepository, times(1)).findById(any());
	}

	@Test
	@DisplayName("채널 업데이트 실패(채널을 찾을 수 없음)")
	void updateChannelFailureChannelNotFound() {
		given(channelRepository.findById(any())).willReturn(Optional.empty());
		assertThatThrownBy(() -> channelService.update(channelId, updateRequest))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@DisplayName("채널 업데이트 실패(private 채널)")
	void updateChannelFailurePrivateChannel() {
		given(channelRepository.findById(any())).willReturn(Optional.of(privateChannel));
		assertThatThrownBy(() -> channelService.update(privateChannel.getId(), updateRequest))
			.isInstanceOf(PrivateChannelUpdateException.class);
	}

	@Test
	@DisplayName("채널 삭제 테스트(메세지 없음)")
	void deleteChannel() {
		given(channelRepository.findById(any())).willReturn(Optional.of(publicChannel));
		given(readStatusRepository.findAllByChannelId(any())).willReturn(List.of(readStatus));
		given(messageRepository.findAll()).willReturn(List.of());

		channelService.delete(channelId);

		verify(channelRepository, times(1)).deleteById(any());
		verify(readStatusRepository, times(1)).findAllByChannelId(any());
		verify(readStatusRepository, times(1)).deleteById(any());
		verify(messageRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("채널 삭제 실패(채널을 찾을 수 없음)")
	void deleteChannelFailureChannelNotFound() {
		given(channelRepository.findById(any())).willReturn(Optional.empty());
		assertThatThrownBy(() -> channelService.delete(channelId))
			.isInstanceOf(ChannelNotFoundException.class);
	}

	@Test
	@DisplayName("userId로 채널 조회 테스트(userId가 null 일 때)")
	void findPublicChannel() {
		ChannelDto publicDto = new ChannelDto(
			publicChannel.getId(),
			publicChannel.getType(),
			publicChannel.getName(),
			publicChannel.getDescription(),
			null,
			null
		);

		List<ChannelDto> channelDtos = List.of(publicDto);

		given(readStatusRepository.findAllByUserId(any())).willReturn(List.of());
		given(channelRepository.findAllByIdInOrType(any(), any())).willReturn(
			List.of(publicChannel));
		given(channelMapper.toDto(any(), any(), any())).willReturn(publicDto);
		given(readStatusRepository.findAllByChannelId(any())).willReturn(List.of());
		given(userRepository.findAllByIdIn(any())).willReturn(List.of());
		given(messageRepository.findTopByChannelIdOrderByCreatedAtDescIdDesc(any())).willReturn(
			Optional.empty());

		List<ChannelDto> result = channelService.findAllByUserId(null);

		assertThat(result).isEqualTo(channelDtos);

		verify(readStatusRepository, times(1)).findAllByUserId(any());
		verify(channelRepository, times(1)).findAllByIdInOrType(any(), any());
		verify(channelMapper, times(1)).toDto(any(), any(), any());
		verify(readStatusRepository, times(1)).findAllByChannelId(any());
		verify(userRepository, times(1)).findAllByIdIn(any());
		verify(messageRepository, times(1)).findTopByChannelIdOrderByCreatedAtDescIdDesc(any());
	}

	@Test
	@DisplayName("userId로 채널 조회 테스트(userId가 있을 때)")
	void findChannelByUserId() {
		ChannelDto publicDto = new ChannelDto(
			publicChannel.getId(),
			publicChannel.getType(),
			publicChannel.getName(),
			publicChannel.getDescription(),
			null,
			null
		);
		ChannelDto privateDto = new ChannelDto(
			privateChannel.getId(),
			privateChannel.getType(),
			privateChannel.getName(),
			privateChannel.getDescription(),
			List.of(userDto),
			null
		);
		List<ChannelDto> channelDtos = List.of(publicDto, privateDto);

		given(readStatusRepository.findAllByUserId(any())).willReturn(List.of(readStatus));
		given(channelRepository.findAllByIdInOrType(any(), any())).willReturn(
			List.of(publicChannel, privateChannel));
		given(channelMapper.toDto(eq(publicChannel), any(), any())).willReturn(publicDto);
		given(channelMapper.toDto(eq(privateChannel), any(), any())).willReturn(privateDto);
		given(readStatusRepository.findAllByChannelId(any())).willReturn(List.of(readStatus));
		given(userRepository.findAllByIdIn(any())).willReturn(List.of(user));
		given(messageRepository.findTopByChannelIdOrderByCreatedAtDescIdDesc(any())).willReturn(
			Optional.empty());

		List<ChannelDto> result = channelService.findAllByUserId(user.getId());

		assertThat(result).isEqualTo(channelDtos);

		verify(readStatusRepository, times(1)).findAllByUserId(any());
		verify(channelRepository, times(1)).findAllByIdInOrType(any(), any());
		verify(channelMapper, times(2)).toDto(any(), any(), any());
		verify(readStatusRepository, times(2)).findAllByChannelId(any());
		verify(userRepository, times(2)).findAllByIdIn(any());
		verify(messageRepository, times(2)).findTopByChannelIdOrderByCreatedAtDescIdDesc(any());
	}

}
