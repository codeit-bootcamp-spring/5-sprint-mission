package com.sprint.mission.discodeit.unit.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserMapper userMapper;

	@Mock
	private BinaryContentRepository binaryContentRepository;

	@Mock
	private BinaryContentStorage binaryContentStorage;

	@InjectMocks
	private BasicUserService userService;

	@Test
	@DisplayName("사용자 생성 테스트 - binaryContext 없음")
	void createUser_noBinaryContext() {
		// given
		UserCreateRequest userCreateRequest = new UserCreateRequest("testName1", "testMail@google.com", "testPassword!123");
		Optional<BinaryContentCreateRequest> binaryContentCreateRequestOptional = Optional.empty();

		given(userRepository.existsByEmail("testMail@google.com")).willReturn(false);
		given(userRepository.existsByUsername("testName1")).willReturn(false);

		User savedUser = new User("testName1", "testMail@google.com", "testPassword!123", null);
		given(userRepository.save(any(User.class))).willReturn(savedUser);

		// binaryContext는 없기에 profile은 null
		UserDto mockDto = new UserDto(UUID.randomUUID(),"testName1", "testMail@google.com", null ,true);
		given(userMapper.toDto(any(User.class))).willReturn(mockDto);

		// when
		UserDto userDto = userService.create(userCreateRequest, binaryContentCreateRequestOptional);

		// then
		assertThat(userDto.email()).isEqualTo(userCreateRequest.email());
		assertThat(userDto.username()).isEqualTo(userCreateRequest.username());

		then(userRepository).should(times(1)).existsByEmail(userCreateRequest.email());
		then(userRepository).should(times(1)).existsByUsername(userCreateRequest.username());
		then(userRepository).should(times(1)).save(any(User.class));
		then(userMapper).should(times(1)).toDto(any(User.class));
		then(binaryContentRepository).shouldHaveNoInteractions();
		then(binaryContentStorage).shouldHaveNoInteractions();
	}

	@Test
	@DisplayName("사용자 생성 테스트 - binaryContent 있음")
	void createUser_withBinaryContext() {
		// given
		UserCreateRequest userCreateRequest = new UserCreateRequest("testName1", "testMail@google.com", "testPassword!123");
		BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
			"testName1.jpg", "image/jpeg", "testContent".getBytes()
		);
		Optional<BinaryContentCreateRequest> binaryContentCreateRequestOptional = Optional.of(binaryContentCreateRequest);

		given(userRepository.existsByEmail(userCreateRequest.email())).willReturn(false);
		given(userRepository.existsByUsername(userCreateRequest.username())).willReturn(false);

		BinaryContent fakeBinary = new BinaryContent(
			binaryContentCreateRequest.fileName(),
			(long) binaryContentCreateRequest.bytes().length,
			binaryContentCreateRequest.contentType()
		);

		given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(fakeBinary);
		given(binaryContentStorage.put(fakeBinary.getId(), binaryContentCreateRequest.bytes())).willReturn(fakeBinary.getId());

		UserDto mockDto = new UserDto(
			UUID.randomUUID(),
			userCreateRequest.username(),
			userCreateRequest.email(),
			new BinaryContentDto(UUID.randomUUID(),
				binaryContentCreateRequest.fileName(),
				(long) binaryContentCreateRequest.bytes().length,
				binaryContentCreateRequest.contentType()
			),
			true
		);
		given(userMapper.toDto(any(User.class))).willReturn(mockDto);

		// when
		UserDto userDto = userService.create(userCreateRequest, binaryContentCreateRequestOptional);

		// then
		assertThat(userDto.email()).isEqualTo(userCreateRequest.email());
		assertThat(userDto.username()).isEqualTo(userCreateRequest.username());

		then(userRepository).should(times(1)).existsByEmail(userCreateRequest.email());
		then(userRepository).should(times(1)).existsByUsername(userCreateRequest.username());
		then(userRepository).should(times(1)).save(any(User.class));
		then(userMapper).should(times(1)).toDto(any(User.class));
		then(binaryContentRepository).should(times(1)).save(any(BinaryContent.class));
		then(binaryContentStorage).should(times(1)).put(fakeBinary.getId(), binaryContentCreateRequest.bytes());
	}

	@Test
	@DisplayName("사용자 생성 테스트 - 중복된 이메일")
	void createUser_withDuplicateEmail() {
		//
		UserCreateRequest userCreateRequest =
			new UserCreateRequest("testName1", "testMail@google.com", "testPassword!123");
		given(userRepository.existsByEmail(userCreateRequest.email())).willReturn(true);
		//
		assertThatThrownBy(() -> userService.create(userCreateRequest, Optional.empty()))
			.isInstanceOf(UserNotFoundException.class)
				.isInstanceOf(DiscodeitException.class);
		//
		then(userRepository).should(times(1)).existsByEmail(userCreateRequest.email());
		then(userRepository).shouldHaveNoMoreInteractions();
	}


	@Test
	@DisplayName("사용자 업데이트 테스트 - 성공")
	void updateUser_success() {
		UUID existingUserId = UUID.randomUUID();
		UserUpdateRequest userUpdateRequest = new UserUpdateRequest("newUsername", "newEmail", "newPassword!123");
		BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest("newProfile.jpg",
			"image/jpeg", "newProfileContent".getBytes());
		Optional<BinaryContentCreateRequest> binaryContentCreateRequestOptional = Optional.of(binaryContentCreateRequest);
		User existingUser = new User("existingUsername", "existingEmail", "existingPassword", null);

		given(userRepository.findById(existingUserId)).willReturn(Optional.of(existingUser));
		given(userRepository.existsByEmail(userUpdateRequest.newEmail())).willReturn(false);
		given(userRepository.existsByUsername(userUpdateRequest.newUsername())).willReturn(false);

		UUID fakeBinaryId = UUID.randomUUID();

		UserDto userDto = new UserDto(existingUserId, "newUsername", "newEmail",
			new BinaryContentDto(fakeBinaryId, binaryContentCreateRequest.fileName(),
				(long)binaryContentCreateRequest.bytes().length, binaryContentCreateRequest.contentType()), true);

		given(userMapper.toDto(any(User.class))).willReturn(userDto);

		// when
		UserDto updatedUser = userService.update(existingUserId, userUpdateRequest, binaryContentCreateRequestOptional);

		// then
		assertThat(updatedUser.username()).isEqualTo(userUpdateRequest.newUsername());
		assertThat(updatedUser.id()).isEqualTo(existingUserId);
		assertThat(updatedUser.email()).isEqualTo(userUpdateRequest.newEmail());
		assertThat(updatedUser.profile().fileName()).isEqualTo(binaryContentCreateRequest.fileName());

		then(userRepository).should(times(1)).findById(existingUserId);
		then(userRepository).should(times(1)).existsByEmail(userUpdateRequest.newEmail());
		then(userRepository).should(times(1)).existsByUsername(userUpdateRequest.newUsername());
		then(userMapper).should(times(1)).toDto(any(User.class));
	}

	@Test
	@DisplayName("사용자 업데이트 테스트 - 실패(username 중복)")
	void updateUser_fail() {
		UUID existingUserId = UUID.randomUUID();
		UserUpdateRequest userUpdateRequest = new UserUpdateRequest("newUsername", "newEmail", "newPassword!123");
		BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest("newProfile.jpg",
			"image/jpeg", "newProfileContent".getBytes());
		Optional<BinaryContentCreateRequest> binaryContentCreateRequestOptional = Optional.of(binaryContentCreateRequest);
		User existingUser = new User("existingUsername", "existingEmail", "existingPassword", null);

		given(userRepository.findById(existingUserId)).willReturn(Optional.of(existingUser));
		given(userRepository.existsByEmail(userUpdateRequest.newEmail())).willReturn(false);
		given(userRepository.existsByUsername(userUpdateRequest.newUsername())).willReturn(true);

		//
		assertThatThrownBy(() -> userService.update(existingUserId, userUpdateRequest, binaryContentCreateRequestOptional))
			.isInstanceOf(UserNotFoundException.class)
			.isInstanceOf(DiscodeitException.class);
		//
		then(userRepository).should(times(1)).existsByEmail(userUpdateRequest.newEmail());
		then(userRepository).should(times(1)).existsByUsername(userUpdateRequest.newUsername());
		then(userRepository).shouldHaveNoMoreInteractions();
	}

	@Test
	@DisplayName("사용자 삭제 테스트 - 성공")
	void deleteUser_success() {
		// given
		UUID userId = UUID.randomUUID();

		given(userRepository.existsById(userId)).willReturn(true);

		//
		userService.delete(userId);

		//
		then(userRepository).should(times(1)).existsById(userId);
		then(userRepository).should(times(1)).deleteById(userId);
	}

	@Test
	@DisplayName("사용자 삭제 테스트 - 실패")
	void deleteUser_fail() {
		// given
		UUID userId = UUID.randomUUID();
		given(userRepository.existsById(userId)).willReturn(false);

		//
		assertThatThrownBy(() -> userService.delete(userId))
			.isInstanceOf(UserNotFoundException.class);

		then(userRepository).should(times(1)).existsById(userId);
		then(userRepository).should(never()).deleteById(userId);
	}

}
