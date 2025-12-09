package com.sprint.mission.discodeit.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sprint.mission.discodeit.dto.user.UserCommand;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.log.LogUtils;
import com.sprint.mission.discodeit.mapper.MultipartFileMapper;
import com.sprint.mission.discodeit.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Validated
@Slf4j
public class UserController {

	private final UserService userService;
	private final MultipartFileMapper multipartFileMapper;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UserDto> create(
		@RequestPart("userCreateRequest") @Valid UserCreateRequest request,
		@RequestPart(required = false) MultipartFile profile
	) throws IOException {
		log.debug("payload: request={}, profile={}",
			request.forLog(), LogUtils.summarizeMultipartFile(profile));

		UserCommand userCommand = new UserCommand(
			request.username(),
			request.email(),
			request.password(),
			multipartFileMapper.toNewBinaryContent(profile));

		UserDto userDto = userService.create(userCommand);
		log.info("User created: {}", userDto.forLog());

		return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
	}

	@PatchMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UserDto> update(
		@PathVariable UUID userId,
		@RequestPart("userUpdateRequest") @Valid UserUpdateRequest request,
		@RequestPart(required = false) MultipartFile profile
	) throws IOException {
		log.debug(
			"payload: id={} request={}, profile={}",
			userId, request.forLog(), LogUtils.summarizeMultipartFile(profile));

		UserCommand userCommand = new UserCommand(
			request.newUsername(),
			request.newEmail(),
			request.newPassword(),
			multipartFileMapper.toNewBinaryContent(profile)
		);

		UserDto userDto = userService.update(userId, userCommand);
		log.info("User updated: {}", userDto.forLog());

		return ResponseEntity.status(HttpStatus.OK).body(userDto);
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> delete(@PathVariable UUID userId) {
		userService.delete(userId);
		log.info("User deleted: id={}", userId);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping
	public ResponseEntity<List<UserDto>> findAll() {
		List<UserDto> userDtos = userService.findAll();

		return ResponseEntity.status(HttpStatus.OK).body(userDtos);
	}
}
