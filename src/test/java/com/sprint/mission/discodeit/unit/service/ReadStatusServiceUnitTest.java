package com.sprint.mission.discodeit.unit.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicReadStatusService;

@ExtendWith(MockitoExtension.class)
public class ReadStatusServiceUnitTest {

	@Mock
	private ReadStatusRepository readStatusRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ChannelRepository channelRepository;

	@Mock
	private ReadStatusMapper readStatusMapper;

	@InjectMocks
	private BasicReadStatusService readStatusService;

	@Test
	@DisplayName("ReadStatusService - create - success")
	void create_success() {
		UUID userId = UUID.randomUUID();
		UUID channelId = UUID.randomUUID();
		Instant lastReadAt = Instant.now();
		ReadStatusCreateRequest req = new ReadStatusCreateRequest(userId, channelId, lastReadAt);

		User user = new User("name", "email@test.com", "pw", null);
		Channel channel = new Channel(ChannelType.PUBLIC, "ch", "desc");
		ReflectionTestUtils.setField(user, "id", userId);
		ReflectionTestUtils.setField(channel, "id", channelId);

		given(userRepository.findById(userId)).willReturn(Optional.of(user));
		given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
		given(readStatusRepository.existsByUserIdAndChannelId(userId, channelId)).willReturn(false);

		ReadStatusDto expected = new ReadStatusDto(UUID.randomUUID(), userId, channelId, lastReadAt);
		given(readStatusMapper.toDto(any(ReadStatus.class))).willReturn(expected);

		//
		ReadStatusDto dto = readStatusService.create(req);

		//
		assertThat(dto).isNotNull();
		assertThat(dto.userId()).isEqualTo(userId);
		assertThat(dto.channelId()).isEqualTo(channelId);
		assertThat(dto.lastReadAt()).isEqualTo(lastReadAt);

		verify(userRepository).findById(userId);
		verify(channelRepository).findById(channelId);
		verify(readStatusRepository).existsByUserIdAndChannelId(userId, channelId);
	}

	@Test
	@DisplayName("ReadStatusService - find - success")
	void find_success() {
		UUID readStatusId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		UUID channelId = UUID.randomUUID();
		Instant at = Instant.now();

		User user = new User("username", "email@email", "pw!123", null);
		Channel channel = new Channel(ChannelType.PUBLIC, "ch", "desc");
		ReflectionTestUtils.setField(user, "id", userId);
		ReflectionTestUtils.setField(channel, "id", channelId);

		ReadStatus entity = new ReadStatus(user, channel, at);
		given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(entity));

		ReadStatusDto expected = new ReadStatusDto(readStatusId, userId, channelId, at);
		given(readStatusMapper.toDto(entity)).willReturn(expected);

		//
		ReadStatusDto dto = readStatusService.find(readStatusId);

		//
		assertThat(dto).isEqualTo(expected);
		verify(readStatusRepository).findById(readStatusId);
		verify(readStatusMapper).toDto(entity);
	}

	@Test
	@DisplayName("ReadStatusService - findAllByUserId - success")
	void findAllByUserId_success() {
		//
		UUID userId = UUID.randomUUID();
		UUID chId1 = UUID.randomUUID();
		UUID chId2 = UUID.randomUUID();
		Instant at1 = Instant.now();
		Instant at2 = Instant.now().minusSeconds(60);

		User user = new User("username", "email@email", "pw!1234", null);
		Channel ch1 = new Channel(ChannelType.PUBLIC, "ch1", "desc1");
		Channel ch2 = new Channel(ChannelType.PUBLIC, "ch2", "desc2");
		ReflectionTestUtils.setField(user, "id", userId);
		ReflectionTestUtils.setField(ch1, "id", chId1);
		ReflectionTestUtils.setField(ch2, "id", chId2);

		ReadStatus rs1 = new ReadStatus(user, ch1, at1);
		ReadStatus rs2 = new ReadStatus(user, ch2, at2);

		given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(rs1, rs2));

		ReadStatusDto d1 = new ReadStatusDto(UUID.randomUUID(), userId, chId1, at1);
		ReadStatusDto d2 = new ReadStatusDto(UUID.randomUUID(), userId, chId2, at2);
		given(readStatusMapper.toDto(rs1)).willReturn(d1);
		given(readStatusMapper.toDto(rs2)).willReturn(d2);

		//
		List<ReadStatusDto> result = readStatusService.findAllByUserId(userId);

		//
		assertThat(result).containsExactly(d1, d2);
		verify(readStatusRepository).findAllByUserId(userId);
		verify(readStatusMapper).toDto(rs1);
		verify(readStatusMapper).toDto(rs2);
	}

	@Test
	@DisplayName("ReadStatusService - update - success")
	void update_success() {
		//
		UUID readStatusId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		UUID channelId = UUID.randomUUID();
		Instant beforeInstant = Instant.now().minusSeconds(300);
		Instant afterInstant = Instant.now();

		User user = new User("username", "email@email", "pw!1234", null);
		Channel channel = new Channel(ChannelType.PUBLIC, "ch", "desc");
		ReflectionTestUtils.setField(user, "id", userId);
		ReflectionTestUtils.setField(channel, "id", channelId);

		ReadStatus entity = new ReadStatus(user, channel, beforeInstant);
		given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(entity));

		ReadStatusDto expected = new ReadStatusDto(readStatusId, userId, channelId, afterInstant);
		given(readStatusMapper.toDto(entity)).willReturn(expected);

		//
		ReadStatusDto dto = readStatusService.update(readStatusId, new ReadStatusUpdateRequest(afterInstant));

		//
		assertThat(entity.getLastReadAt()).isEqualTo(afterInstant);
		assertThat(dto).isEqualTo(expected);

		verify(readStatusRepository).findById(readStatusId);
		verify(readStatusMapper).toDto(entity);
	}

	@Test
	@DisplayName("ReadStatusService - delete - success")
	void delete_success() {
		UUID readStatusId = UUID.randomUUID();
		given(readStatusRepository.existsById(readStatusId)).willReturn(true);

		//
		readStatusService.delete(readStatusId);

		//
		verify(readStatusRepository).existsById(readStatusId);
		verify(readStatusRepository).deleteById(readStatusId);
	}
}
