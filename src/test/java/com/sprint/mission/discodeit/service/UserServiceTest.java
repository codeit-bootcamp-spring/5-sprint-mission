package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.binarycontent.NewBinaryContent;
import com.sprint.mission.discodeit.dto.user.UserCommand;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private BinaryContentRepository binaryContentRepository;

	@Mock
	private BinaryContentStorage binaryContentStorage;

	@Mock
	private UserMapper userMapper;

	@InjectMocks
	private BasicUserService userService;

	private User user;
	private UserDto userDto;
	private UserCommand userCommand;
	private String username;
	private String password;
	private String email;
	private Optional<NewBinaryContent> newBinaryContent;
	private BinaryContent binaryContent;
	private BinaryContentDto binaryContentDto;

	@BeforeEach
	public void setUp() {
		username = "test";
		password = "12341234";
		email = "test@email.com";
		user = new User(username, email, password, null);
		userDto = UserDto.builder()
			.id(user.getId())
			.username(username)
			.email(email)
			.profile(null)
			.online(false)
			.build();
		userCommand = new UserCommand(
			username,
			email,
			password,
			Optional.empty()
		);
		newBinaryContent = Optional.of(new NewBinaryContent(
			"profile",
			"image/jpg",
			"jpg".getBytes(StandardCharsets.UTF_8)
		));
		binaryContent = new BinaryContent(
			"profile",
			"image/jpg",
			"jpg".getBytes().length);
		binaryContentDto = new BinaryContentDto(
			binaryContent.getId(),
			"profile",
			(long)"jpg".getBytes().length,
			"image/jpg"
		);

	}

	@Test
	@DisplayName("사용자 생성 테스트(프로필 사진 없음)")
	void createUserWithoutProfileSuccess() {
		given(userRepository.existsByUsername(username)).willReturn(false);
		given(userRepository.existsByEmail(email)).willReturn(false);
		given(userMapper.toDto(any())).willReturn(userDto);
		given(userRepository.save(any())).willReturn(user);

		UserDto result = userService.create(userCommand);

		assertThat(result).isEqualTo(userDto);
		verify(userRepository, times(1)).save(any());
		verify(userRepository, times(1)).existsByUsername(username);
		verify(userRepository, times(1)).existsByEmail(email);
		verify(userMapper, times(1)).toDto(any());
	}

	@Test
	@DisplayName("사용자 생성 실패(존재하는 username)")
	void createUserFailureWithUsername() {
		given(userRepository.existsByUsername(username)).willReturn(true);
		assertThatThrownBy(() -> userService.create(userCommand))
			.isInstanceOf(UserAlreadyExistsException.class);
	}

	@Test
	@DisplayName("사용자 생성 실패(존재하는 email)")
	void createUserFailureWithEmail() {
		given(userRepository.existsByEmail(email)).willReturn(true);
		assertThatThrownBy(() -> userService.create(userCommand))
			.isInstanceOf(UserAlreadyExistsException.class);
	}

	@Test
	@DisplayName("사용자 정보 수정 테스트(프로필 사진 있음)")
	void updateUserSuccessWithProfile() {
		given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

		username = "updated";
		password = "updated1234";
		email = "updated@email.com";
		userCommand = new UserCommand(username,
			email, password, newBinaryContent);
		user = new User(username, email, password, binaryContent);
		userDto = new UserDto(user.getId(), username, email, binaryContentDto, false, Role.USER);
		given(userMapper.toDto(any())).willReturn(userDto);

		given(userRepository.existsByUsername(username)).willReturn(false);
		given(userRepository.existsByEmail(email)).willReturn(false);
		given(userRepository.save(any())).willReturn(user);
		given(binaryContentRepository.save(any())).willReturn(binaryContent);
		given(binaryContentStorage.put(any(), any())).willReturn(binaryContent.getId());

		UserDto result = userService.update(user.getId(), userCommand);

		assertThat(result).isEqualTo(userDto);
		verify(userRepository, times(1)).findById(user.getId());
		verify(userRepository, times(1)).existsByUsername(username);
		verify(userRepository, times(1)).existsByEmail(email);
		verify(binaryContentRepository, times(1)).save(any());
		verify(binaryContentStorage, times(1)).put(any(), any());
		verify(userMapper, times(1)).toDto(any());
	}

	@Test
	@DisplayName("사용자 정보 수정 실패(존재하지 않는 사용자 id)")
	void updateUserFailureWithId() {
		given(userRepository.findById(any())).willReturn(Optional.empty());
		assertThatThrownBy(() -> userService.update(UUID.randomUUID(), userCommand))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@DisplayName("사용자 삭제 테스트(프로필 사진 있음)")
	void deleteUserSuccess() {
		user = new User(username, email, password, binaryContent);
		given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

		userService.delete(user.getId());

		verify(userRepository, times(1)).findById(user.getId());
		verify(binaryContentRepository, times(1)).deleteById(user.getProfile().getId());
		verify(userRepository, times(1)).deleteById(user.getId());
	}

	@Test
	@DisplayName("사용자 삭제 실패(존재하지 않는 사용자)")
	void deleteUserFailureWithId() {
		given(userRepository.findById(user.getId())).willReturn(Optional.empty());
		assertThatThrownBy(() -> userService.delete(user.getId()))
			.isInstanceOf(UserNotFoundException.class);
	}
}
