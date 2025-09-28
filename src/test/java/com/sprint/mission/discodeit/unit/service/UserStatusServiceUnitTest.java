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

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

@ExtendWith(MockitoExtension.class)
public class UserStatusServiceUnitTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserStatusMapper userStatusMapper;

	@Mock
	private UserStatusRepository userStatusRepository;




	@InjectMocks
	private BasicUserStatusService userStatusService;

	@Test
	@DisplayName("UserStatusServiceUnitTest - create - success")
	public void create_success(){
		//
		UUID userId = UUID.randomUUID();
		Instant lastActiveAt = Instant.now();
		UserStatusCreateRequest req = new UserStatusCreateRequest(userId, lastActiveAt);

		User user = new User("name", "email@test.com", "password", null);
		ReflectionTestUtils.setField(user, "id", userId);
		UserStatusDto expectedDto = new UserStatusDto(UUID.randomUUID(), userId, lastActiveAt);

		given(userRepository.findById(userId)).willReturn(Optional.of(user));
		given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(expectedDto);

		//
		UserStatusDto dto = userStatusService.create(req);

		//
		assertThat(dto).isNotNull();
		assertThat(dto.userId()).isEqualTo(userId);
		assertThat(dto.lastActiveAt()).isEqualTo(lastActiveAt);

		verify(userStatusRepository).save(any(UserStatus.class));
	}


	@Test
	@DisplayName("UserStatusServiceUnitTest - find - success")
	public void find_success(){
		UUID userStatusId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		Instant lastActiveAt = Instant.now();

		User user = new User("name", "email@test.com", "password!123", null);
		ReflectionTestUtils.setField(user, "id", userId);

		UserStatus entity = new UserStatus(user, lastActiveAt);
		UserStatusDto expected = new UserStatusDto(userStatusId, userId, lastActiveAt);

		given(userStatusRepository.findById(userStatusId)).willReturn(Optional.of(entity));
		given(userStatusMapper.toDto(entity)).willReturn(expected);

		//
		UserStatusDto dto = userStatusService.find(userStatusId);

		//
		assertThat(dto).isNotNull();
		assertThat(dto.id()).isEqualTo(userStatusId);
		assertThat(dto.userId()).isEqualTo(userId);
		assertThat(dto.lastActiveAt()).isEqualTo(lastActiveAt);

		verify(userStatusRepository).findById(userStatusId);
		verify(userStatusMapper).toDto(entity);
	}

	@Test
	@DisplayName("UserStatusServiceUnitTest - findAll - success")
	public void findAll_success(){
		User user1 = new User("u1", "u1@test.com", "pw", null);
		User user2 = new User("u2", "u2@test.com", "pw", null);
		UUID userId1 = UUID.randomUUID();
		UUID userId2 = UUID.randomUUID();
		ReflectionTestUtils.setField(user1, "id", userId1);
		ReflectionTestUtils.setField(user2, "id", userId2);

		Instant instant1 = Instant.now();
		Instant instant2 = Instant.now().minusSeconds(30);

		UserStatus userStatus1 = new UserStatus(user1, instant1);
		UserStatus userStatus2 = new UserStatus(user2, instant2);

		given(userStatusRepository.findAll()).willReturn(List.of(userStatus1, userStatus2));

		UserStatusDto userStatusDto1 = new UserStatusDto(UUID.randomUUID(), userId1, instant1);
		UserStatusDto userStatusDto2 = new UserStatusDto(UUID.randomUUID(), userId2, instant2);

		given(userStatusMapper.toDto(userStatus1)).willReturn(userStatusDto1);
		given(userStatusMapper.toDto(userStatus2)).willReturn(userStatusDto2);

		//
		List<UserStatusDto> result = userStatusService.findAll();

		//
		assertThat(result).hasSize(2);
		assertThat(result).containsExactly(userStatusDto1, userStatusDto2);

		verify(userStatusRepository).findAll();
		verify(userStatusMapper).toDto(userStatus1);
		verify(userStatusMapper).toDto(userStatus2);
	}

	@Test
	@DisplayName("UserStatusServiceUnitTest - update - success")
	public void update_success(){
		//
		UUID statusId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		Instant beforeInstant = Instant.now().minusSeconds(300);
		Instant afterInstant = Instant.now();

		User user = new User("name", "email@test.com", "pw", null);
		ReflectionTestUtils.setField(user, "id", userId);
		UserStatus userStatus = new UserStatus(user, beforeInstant);

		given(userStatusRepository.findById(statusId)).willReturn(Optional.of(userStatus));

		UserStatusDto expected = new UserStatusDto(statusId, userId, afterInstant);
		given(userStatusMapper.toDto(userStatus)).willReturn(expected);

		//
		UserStatusDto dto = userStatusService.update(statusId, new UserStatusUpdateRequest(afterInstant));

		//
		assertThat(userStatus.getLastActiveAt()).isEqualTo(afterInstant);
		assertThat(dto).isEqualTo(expected);

		verify(userStatusRepository).findById(statusId);
		verify(userStatusMapper).toDto(userStatus);
	}
}
