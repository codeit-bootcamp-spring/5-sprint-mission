package service;

import static com.sprint.mission.discodeit.domain.enums.ChannelType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sprint.mission.discodeit.domain.dto.CreatePrivateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.CreatePublicChannelDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ParticipantsEmptyException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceTest {

	@InjectMocks
	private BasicChannelService channelService;

	@Mock
	private ChannelRepository channelRepository;
	@Mock
	private MessageRepository messageRepository;
	@Mock
	private ReadStatusRepository readStatusRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ChannelMapper channelMapper;
	@Mock
	private UserStatusRepository userStatusRepository;
	@Mock
	private UserMapper userMapper;
	@Mock
	BinaryContentMapper binaryContentMapper;

	@Test
	@DisplayName("PUBLIC 채널 생성 테스트 올바른 값이 주어졌을 때 ")
	public void channelCreateTestWithRightInput() {

		// Given
		String name = "newChannelName";
		String description = "newChannelDescription";
		Channel MockChannel = Channel.builder()
		  .type(PUBLIC)
		  .name(name)
		  .description(description)
		  .build();
		ChannelDto MockChannelDto = ChannelDto.builder()
		  .id(null)
		  .type(PUBLIC)
		  .name(name)
		  .description(description)
		  .participants(List.of())
		  .lastMessageAt(null)
		  .build();
		given(channelRepository.save(any())).willReturn(MockChannel);
		given(readStatusRepository.findAllByChannelId(any())).willReturn(List.of());
		given(userStatusRepository.findByUserIdIn(any())).willReturn(List.of());
		given(channelMapper.toDto(any(), any(), any())).willReturn(MockChannelDto);

		CreatePublicChannelDTO command = CreatePublicChannelDTO.builder()
		  .name(name)
		  .description(description)
		  .build();

		// When
		ChannelDto result = channelService.createPublic(command);

		// Then
		assertAll(
		  () -> assertThat(result.getName()).isEqualTo(name),
		  () -> assertThat(result.getType()).isEqualTo(PUBLIC),
		  () -> assertThat(result.getDescription()).isEqualTo(description)
		);

	}

	@Test
	@DisplayName("PRIVATE 채널 생성 테스트 올바른 값이 주어졌을 때 ")
	public void privateChannelCreateTestWithRightInput() {

		BinaryContentDto mockBinaryContentDto = BinaryContentDto.builder().
		  id(UUID.randomUUID())
		  .fileName("filename")
		  .size(123L)
		  .contentType("image")
		  .build();

		UUID id1 = UUID.randomUUID();
		UUID id2 = UUID.randomUUID();
		UUID id3 = UUID.randomUUID();
		User mockUser1 = User.builder()
		  .id(id1)
		  .username("mockUser1")
		  .email("mockUser1@email")
		  .password("mockUser1PW")
		  .profileImage(null)
		  .build();
		UserStatus mockUserStatus1 = new UserStatus(mockUser1);

		UserDto mockUserDto1 = UserDto.builder()
		  .id(id1)
		  .username("mockUser1")
		  .email("mockUser1@email")
		  .profile(mockBinaryContentDto)
		  .online(false)
		  .build();

		User mockUser2 = User.builder()
		  .id(id2)
		  .username("mockUser2")
		  .email("mockUser2@email")
		  .password("mockUser2PW")
		  .profileImage(null)
		  .build();
		UserStatus mockUserStatus2 = new UserStatus(mockUser2);

		UserDto mockUserDto2 = UserDto.builder()
		  .id(id2)
		  .username("mockUser2")
		  .email("mockUser2@email")
		  .profile(mockBinaryContentDto)
		  .online(false)
		  .build();

		User mockUser3 = User.builder()
		  .id(id3)
		  .username("mockUser3")
		  .email("mockUser3@email")
		  .password("mockUser3PW")
		  .profileImage(null)
		  .build();
		UserStatus mockUserStatus3 = new UserStatus(mockUser3);

		UserDto mockUserDto3 = UserDto.builder()
		  .id(id3)
		  .username("mockUser3")
		  .email("mockUser3@email")
		  .profile(mockBinaryContentDto)
		  .online(false)
		  .build();

		CreatePrivateChannelDTO command = CreatePrivateChannelDTO.builder()
		  .userIds(List.of(id1, id2, id3))
		  .build();

		Channel mockChannel = Channel.builder()
		  .type(PRIVATE)
		  .build();

		ChannelDto mockChannelDto = ChannelDto.builder()
		  .id(UUID.randomUUID())
		  .type(PRIVATE)
		  .name(null)
		  .description(null)
		  .participants(List.of(mockUserDto1, mockUserDto2, mockUserDto3))
		  .lastMessageAt(null)
		  .build();

		given(channelRepository.save(any())).willReturn(mockChannel);
		given(userRepository.findUsersWithProfileByIdIn(any())).willReturn(List.of(mockUser1, mockUser2, mockUser3));
		given(readStatusRepository.saveAll(any())).willReturn(null);
		given(userStatusRepository.findByUserIdIn(any()))
		  .willReturn(List.of(mockUserStatus1, mockUserStatus2, mockUserStatus3));
		given(binaryContentMapper.toDto(any())).willReturn(mockBinaryContentDto);
		given(binaryContentMapper.toDto(any())).willReturn(mockBinaryContentDto);
		given(binaryContentMapper.toDto(any())).willReturn(mockBinaryContentDto);
		given(channelMapper.toDto(any(), any(), any())).willReturn(mockChannelDto);

		// when
		ChannelDto channelDto = channelService.createPrivate(command);
		assertAll(
		  () -> assertThat(channelDto.getType()).isEqualTo(PRIVATE),
		  () -> assertThat(channelDto.getParticipants().size()).isEqualTo(3),
		  () -> assertThat(channelDto.getParticipants().containsAll
			(List.of(mockUserDto1, mockUserDto2, mockUserDto3))).isTrue()

		);

	}

	@Test
	@DisplayName("PRIVATE 채널 생성 테스트 빈 참여자 목록이 주어졌을 때 ")
	public void privateChannelCreateTestWithWrongInput() {

		// Given
		Channel mockChannel = Channel.builder()
		  .type(PRIVATE)
		  .build();
		given(channelRepository.save(any())).willReturn(mockChannel);
		given(userRepository.findUsersWithProfileByIdIn(any())).willReturn(List.of());

		CreatePrivateChannelDTO command = CreatePrivateChannelDTO.builder()
		  .userIds(List.of())
		  .build();
		// when & then
		assertThatThrownBy(() -> {
			channelService.createPrivate(command);
		})
		  .isInstanceOf(ParticipantsEmptyException.class)   // 예외 타입 검증
		  .hasMessageContaining("참여자는 1명 이상이어야합니다.");      // Optional: 메시지 검증

	}

	@Test
	@DisplayName("채널 조회 테스트 빈 올바른 입력이 주어졌을 때 ")
	public void ReadChannelByUserTestWithRightInput() {

		UUID id1 = UUID.randomUUID();
		Channel mockPublicChannel1 = Channel.builder()
		  .id(UUID.randomUUID())
		  .type(PUBLIC)
		  .name("publicChannel1")
		  .description("Description1")
		  .build();

		Channel mockPublicChannel2 = Channel.builder()
		  .id(UUID.randomUUID())
		  .type(PUBLIC)
		  .name("publicChannel2")
		  .description("Description2")
		  .build();

		Channel mockPrivateChannel1 = Channel.builder()
		  .id(UUID.randomUUID())
		  .type(PRIVATE)
		  .build();

		User mockUser = User.builder()
		  .id(id1)
		  .username("mockUser1")
		  .email("mockUser1@email").build();
		UserDto mockUserDto = UserDto.builder()
		  .id(id1)
		  .username(mockUser.getUsername())
		  .email(mockUser.getEmail())
		  .profile(null)
		  .online(false)
		  .build();
		ChannelDto mockPublicChannelDto1 = ChannelDto.builder()
		  .id(mockPublicChannel1.getId())
		  .type(PUBLIC)
		  .name(mockPublicChannel1.getName())
		  .description(mockPublicChannel1.getDescription())
		  .participants(List.of())
		  .lastMessageAt(null)
		  .build();
		ChannelDto mockPublicChannelDto2 = ChannelDto.builder()
		  .id(mockPublicChannel2.getId())
		  .type(PUBLIC)
		  .name(mockPublicChannel2.getName())
		  .description(mockPublicChannel2.getDescription())
		  .participants(List.of())
		  .lastMessageAt(null)
		  .build();
		ChannelDto mockPrivateChannelDto1 = ChannelDto.builder()
		  .id(mockPrivateChannel1.getId())
		  .type(PRIVATE)
		  .name(mockPrivateChannel1.getName())
		  .description(mockPrivateChannel1.getDescription())
		  .participants(List.of())
		  .lastMessageAt(null)
		  .build();

		ReadStatus mockReadStatus1 = new ReadStatus(mockUser, mockPrivateChannel1);

		given(channelRepository.findPublicChannels(any())).willReturn(List.of(mockPublicChannel1, mockPublicChannel2));
		given(readStatusRepository.findAllByUserId(any())).willReturn(List.of(mockReadStatus1));
		given(readStatusRepository.findReadStatusDetailAllByChannelIds(any())).willReturn(List.of(mockReadStatus1));
		given(userStatusRepository.findByUserIdIn(any())).willReturn(List.of());
		given(channelMapper.toDto(eq(mockPublicChannel1), any(), any()))
		  .willReturn(mockPublicChannelDto1);
		given(channelMapper.toDto(eq(mockPublicChannel2), any(), any()))
		  .willReturn(mockPublicChannelDto2);
		given(channelMapper.toDto(eq(mockPrivateChannel1), any(), any()))
		  .willReturn(mockPrivateChannelDto1);
		given(userMapper.toDto(any(), anyBoolean(), any())).willReturn(mockUserDto);
		given(binaryContentMapper.toDto(any())).willReturn(null);

		//when
		List<ChannelDto> channelDtos = channelService.readAllByUserId(id1);

		channelDtos.forEach(System.out::println);
		assertThat(channelDtos)
		  .hasSize(3)
		  .containsExactlyInAnyOrder(mockPublicChannelDto1, mockPublicChannelDto2, mockPrivateChannelDto1);

	}

	@Test
	@DisplayName("채널 삭제 테스트 올바른 입력이 주어졌을 때 ")
	public void deleteChannelTestWithRightInput() {
		// Given
		given(channelRepository.existsById(any())).willReturn(true);
		doNothing().when(messageRepository).deleteByChannelId(any());
		doNothing().when(readStatusRepository).deleteByChannelId(any());
		doNothing().when(channelRepository).deleteById(any());

		// When
		boolean result = channelService.delete(UUID.randomUUID());

		assertThat(result).isTrue();

	}

	@Test
	@DisplayName("채널 삭제 테스트 잘못된 입력이 주어졌을 때 ")
	public void deleteChannelTestWithWrongInput() {
		// Given
		given(channelRepository.existsById(any())).willReturn(false);

		// when & then
		assertThatThrownBy(() -> {
			channelService.delete(UUID.randomUUID());
		})
		  .isInstanceOf(ChannelNotFoundException.class)   // 예외 타입 검증
		  .hasMessageContaining("채널을 찾을 수 없습니다.");

	}

	@Test
	@DisplayName("채널 갱신 테스트 각 필드가 모두 수정 되었을 때 ")
	public void updateChannelTestWithWrongInput() {

		// Given
		String oldChannelName = "oldChannelName";
		String oldDescription = "oldDescription";
		String newChannelName = "newChannelName";
		String newDescription = "newDescription";

		Channel mockOldChannel = Channel.builder()
		  .id(UUID.randomUUID())
		  .type(PUBLIC)
		  .name(oldChannelName)
		  .description(oldDescription)
		  .build();

		ChannelDto mockChannelDto = ChannelDto.builder()
		  .id(mockOldChannel.getId())
		  .type(PUBLIC)
		  .name(newChannelName)
		  .description(newDescription)
		  .participants(List.of())
		  .lastMessageAt(null)
		  .build();

		given(channelRepository.findById(any())).willReturn(Optional.of(mockOldChannel));
		given(channelRepository.save(any())).willReturn(null);
		given(readStatusRepository.findAllByChannelId(any())).willReturn(List.of());
		given(userStatusRepository.findByUserIdIn(any())).willReturn(List.of());
		given(channelMapper.toDto(any(), any(), any())).willReturn(mockChannelDto);

		UpdateChannelDTO command = UpdateChannelDTO.builder()
		  .id(mockOldChannel.getId())
		  .name(newChannelName)
		  .description(newDescription)
		  .build();

		//When
		ChannelDto result = channelService.update(command);

		assertAll(
		  () -> assertThat(result.getId()).isEqualTo(mockOldChannel.getId()),
		  () -> assertThat(result.getName()).isEqualTo(newChannelName),
		  () -> assertThat(result.getDescription()).isEqualTo(newDescription)
		);

	}
}

