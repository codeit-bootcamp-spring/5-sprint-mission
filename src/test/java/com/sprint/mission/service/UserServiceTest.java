package com.sprint.mission.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import com.sprint.mission.discodeit.domain.dto.CreateBiContentDTO;
import com.sprint.mission.discodeit.domain.dto.CreateUserDTO;
import com.sprint.mission.discodeit.domain.dto.UpdateUserDTO;
import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.DuplicateUserNameOrEmailException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

@ExtendWith(MockitoExtension.class)
@EnableJpaAuditing
@ActiveProfiles("test")
public class UserServiceTest {
	@InjectMocks
	private BasicUserService userService;

	@Mock
	private BinaryContentService binaryContentService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private BinaryContentRepository binaryContentRepository;
	@Mock
	private UserStatusRepository userStatusRepository;
	@Mock
	private BinaryContentStorage binaryContentStorage;
	@Mock
	private UserMapper userMapper;
	@Mock
	private BinaryContentMapper binaryContentMapper;

	String username;
	String email;
	String password;

	String fileName;
	String contentType;
	long size;
	byte[] content;

	BinaryContent mockBinaryContent;
	User mockUser;
	CreateBiContentDTO mockCreateBiContentDto;
	BinaryContentDto mockBinaryContentDto;

	@BeforeEach
	public void setUp() {
		username = "username";
		email = "email";
		password = "password";

		fileName = "fileName";
		contentType = "contentType";
		size = 123L;
		content = "contents".getBytes();

		mockBinaryContent = BinaryContent.builder()
		  .id(UUID.randomUUID())
		  .fileName(fileName)
		  .contentType(contentType)
		  .size(size)
		  .build();
		mockCreateBiContentDto = CreateBiContentDTO.builder()
		  .content(content)
		  .size(size)
		  .fileName(fileName)
		  .build();
		mockBinaryContentDto = BinaryContentDto.builder()
		  .id(mockBinaryContent.getId())
		  .fileName(fileName)
		  .size(size)
		  .contentType(contentType)
		  .build();
		mockUser = User.builder()
		  .id(UUID.randomUUID())
		  .username(username)
		  .email(email)
		  .password(password)
		  .profileImage(mockBinaryContent)
		  .build();

	}

	@Test
	@DisplayName("사용자 생성 테스트 아바타가 주어졌을 때 ")
	public void userCreateTestWithRightInput() {

		BinaryContent mockProfileImage = new BinaryContent(size, contentType, fileName);

		CreateUserDTO command = CreateUserDTO.builder()
		  .username(username)
		  .email(email)
		  .password(password)
		  .binaryContent(mockCreateBiContentDto)
		  .build();

		UserDto mockUserDto1 = UserDto.builder()
		  .id(UUID.randomUUID())
		  .username(username)
		  .email(email)
		  .profile(mockBinaryContentDto)
		  .online(true)
		  .build();

		given(userRepository.findByUsername(any())).willReturn(Optional.empty());
		given(userRepository.findByEmail(any())).willReturn(Optional.empty());
		given(binaryContentService.create(any())).willReturn(mockProfileImage);
		given(userRepository.save(any())).willReturn(null);
		given(userStatusRepository.save(any())).willReturn(null);
		given(binaryContentMapper.toDto(any())).willReturn(mockBinaryContentDto);
		given(userMapper.toDto(any(), anyBoolean(), any())).willReturn(mockUserDto1);

		// When
		UserDto result = userService.create(command);

		Assertions.assertAll(
		  () -> assertThat(result.getId()).isEqualTo(mockUserDto1.getId()),
		  () -> assertThat(result.getUsername()).isEqualTo(username),
		  () -> assertThat(result.getEmail()).isEqualTo(email),
		  () -> assertThat(result.getOnline()).isEqualTo(mockUserDto1.getOnline()),
		  () -> assertThat(result.getProfile()).isEqualTo(mockBinaryContentDto)

		);

	}

	@Test
	@DisplayName("사용자 생성 테스트 - username 중복")
	public void userCreateTestWithDuplicateUsername() {

		CreateUserDTO command = CreateUserDTO.builder()
		  .username(username)
		  .email(email)
		  .password("mockUserPW")
		  .binaryContent(null)
		  .build();

		given(userRepository.findByUsername(username)).willReturn(Optional.of(mockUser));

		assertThatThrownBy(() -> userService.create(command))
		  .isInstanceOf(DuplicateUserNameOrEmailException.class)
		  .hasMessageContaining("이미 존재하는 사용자이름 또는 이메일입니다.");
	}

	@Test
	@DisplayName("사용자 생성 테스트 - email 중복")
	public void userCreateTestWithDuplicateEmail() {

		CreateUserDTO command = CreateUserDTO.builder()
		  .username(username)
		  .email(email)
		  .password("mockUserPW")
		  .binaryContent(null)
		  .build();

		given(userRepository.findByUsername(username)).willReturn(Optional.empty());
		given(userRepository.findByEmail(email)).willReturn(Optional.of(mockUser));

		assertThatThrownBy(() -> userService.create(command))
		  .isInstanceOf(DuplicateUserNameOrEmailException.class)
		  .hasMessageContaining("이미 존재하는 사용자이름 또는 이메일입니다.");
	}

	@Test
	@DisplayName("사용자 삭제 테스트 올바른 입력값")
	public void deleteUserTestWithRightValue() {

		given(userRepository.findById(any())).willReturn(Optional.of(mockUser));
		doNothing().when(userStatusRepository).deleteByUserId(any());
		given(binaryContentRepository.findById(any())).willReturn(Optional.of(mockBinaryContent));
		doNothing().when(binaryContentRepository).deleteById(any());
		given(binaryContentStorage.put(any(), any(), null)).willReturn(UUID.randomUUID());
		doNothing().when(userRepository).deleteById(any());

		// When
		userService.delete(mockUser.getId());

	}

	@Test
	@DisplayName("사용자 삭제 테스트 잘못된 입력값")
	public void deleteUserTestWithWrongValue() {

		given(userRepository.findById(any())).willReturn(Optional.empty());

		assertThatThrownBy(() -> userService.delete(UUID.randomUUID()))
		  .isInstanceOf(UserNotFoundException.class)
		  .hasMessageContaining("사용자를 찾을 수 없습니다.");

	}

	@Test
	@DisplayName("사용자 갱신 테스트 모든 필드가 수정되었을 때 입력값")
	public void updateUserTestWithRightValue() {

		long size = 123L;

		String newUsername = "newUsername";
		String newEmail = "newEmail";
		String newPassword = "newPassword";
		String newFilename = "newFilename";
		String newContentType = "newContentType";
		long newSize = 123123L;
		byte[] newContent = "newContent".getBytes();
		CreateBiContentDTO newProfile = CreateBiContentDTO.builder()
		  .content(newContent)
		  .fileName(newFilename)
		  .contentType(newContentType)
		  .size(newSize)
		  .build();
		BinaryContent newBinaryContent = BinaryContent.builder()
		  .id(UUID.randomUUID())
		  .fileName(newFilename)
		  .contentType(newContentType)
		  .size(newSize)
		  .build();
		BinaryContentDto newBinaryContentDto = BinaryContentDto.builder()
		  .id(newBinaryContent.getId())
		  .fileName(newBinaryContent.getFileName())
		  .size(newBinaryContent.getSize())
		  .contentType(newBinaryContent.getContentType())
		  .build();

		UserDto newUserDto = UserDto.builder()
		  .id(mockUser.getId())
		  .username(newUsername)
		  .email(newEmail)
		  .profile(newBinaryContentDto)
		  .online(false)
		  .build();

		UserStatus mockUserStatus = new UserStatus(mockUser);

		given(userRepository.findById(any())).willReturn(Optional.of(mockUser));
		given(userRepository.existsByUsername(any())).willReturn(false);
		given(userRepository.existsByEmail(any())).willReturn(false);
		given(userRepository.existsByEmail(any())).willReturn(false);
		doNothing().when(binaryContentRepository).deleteById(any());
		given(binaryContentStorage.put(any(), any(), any())).willReturn(UUID.randomUUID());
		given(binaryContentRepository.save(any())).willReturn(newBinaryContent);
		given(binaryContentStorage.put(any(), any(), any())).willReturn(UUID.randomUUID());
		given(userRepository.save(any())).willReturn(null);
		given(userStatusRepository.findByUserId(any())).willReturn(Optional.of(mockUserStatus));
		given(binaryContentMapper.toDto(any())).willReturn(newBinaryContentDto);
		given(userMapper.toDto(any(), anyBoolean(), any())).willReturn(newUserDto);

		UpdateUserDTO command = UpdateUserDTO.builder()
		  .userId(mockUser.getId())
		  .newUsername(newUsername)
		  .newEmail(newEmail)
		  .newPassword(newPassword)
		  .newProfilePicture(newProfile)
		  .build();

		// When
		UserDto result = userService.update(command);
		Assertions.assertAll(
		  () -> assertThat(result.getId()).isEqualTo(mockUser.getId()),
		  () -> assertThat(result.getUsername()).isEqualTo(newUsername),
		  () -> assertThat(result.getEmail()).isEqualTo(newEmail),
		  () -> assertThat(result.getProfile()).isEqualTo(newBinaryContentDto)
		);

	}

	@Test
	@DisplayName("사용자 조회 테스트")
	public void readUserTestWithRightValue() {
		User mockUser2 = User.builder()
		  .id(UUID.randomUUID())
		  .username(username + "2")
		  .email(email + "2")
		  .password(password + "2")
		  .profileImage(null)
		  .build();

		User mockUser3 = User.builder()
		  .id(UUID.randomUUID())
		  .username(username + "3")
		  .email(email + "3")
		  .password(password + "3")
		  .profileImage(null)
		  .build();

		UserDto mockUserDto1 = UserDto.builder()
		  .id(mockUser.getId())
		  .username(mockUser.getUsername())
		  .email(mockUser.getEmail())
		  .profile(mockBinaryContentDto)
		  .online(false)
		  .build();

		UserDto mockUserDto2 = UserDto.builder()
		  .id(mockUser2.getId())
		  .username(mockUser2.getUsername())
		  .email(mockUser2.getEmail())
		  .profile(null)
		  .online(false)
		  .build();

		UserDto mockUserDto3 = UserDto.builder()
		  .id(mockUser3.getId())
		  .username(mockUser3.getUsername())
		  .email(mockUser3.getEmail())
		  .profile(null)
		  .online(false)
		  .build();

		UserStatus mockUserStatus1 = new UserStatus(mockUser);
		mockUserStatus1.setLastActiveAt(Instant.now());
		UserStatus mockUserStatus2 = new UserStatus(mockUser2);
		UserStatus mockUserStatus3 = new UserStatus(mockUser3);

		given(userRepository.findUserDetailsAll()).willReturn(List.of(mockUser, mockUser2, mockUser3));
		given(userStatusRepository.findByUserIdIn(any()))
		  .willReturn(List.of(mockUserStatus1, mockUserStatus2, mockUserStatus3));
		given(binaryContentMapper.toDto(null)).willReturn(null);
		given(binaryContentMapper.toDto(mockUser.getProfileImage())).willReturn(mockBinaryContentDto);
		given(userMapper.toDto(mockUser, true, mockBinaryContentDto)).willReturn(mockUserDto1);
		given(userMapper.toDto(mockUser2, false, null)).willReturn(mockUserDto2);
		given(userMapper.toDto(mockUser3, false, null)).willReturn(mockUserDto3);

		// When
		userService.readAll();

	}

}
