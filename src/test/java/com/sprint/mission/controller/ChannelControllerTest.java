package com.sprint.mission.controller;

import static com.sprint.mission.discodeit.exception.ErrorCode.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.ChannelController;
import com.sprint.mission.discodeit.domain.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.domain.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.domain.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.enums.ChannelType;
import com.sprint.mission.discodeit.domain.request.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.domain.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;

@ActiveProfiles("test")
@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

	@MockitoBean
	ChannelService channelService;
	@MockitoBean
	ChannelMapper channelMapper;
	@MockitoBean
	private JpaMetamodelMappingContext jpaMetamodelMappingContext;

	@Autowired
	MockMvc mockMvc; // http 요청과 응답을 처리해출 mock 객체

	@Autowired
	ObjectMapper objectMapper; // json 처리 도와줄 mapper

	UUID channelId;
	ChannelType publicChannelType;
	ChannelType privateChannelType;
	String channelName;
	String description;
	List<User> participants;
	List<UserDto> participantsDto;
	Instant lastMessageAt;

	String username1;
	String email1;
	String password1;

	String username2;
	String email2;
	String password2;

	User user1;
	User user2;
	UserDto userDto1;
	UserDto userDto2;

	BinaryContent binaryContent;
	BinaryContentDto binaryContentDto;

	byte[] bytes;
	long size;
	String contentType;
	String filename;

	@BeforeEach
	void setUp() {
		username1 = "username1";
		email1 = "email1";
		password1 = "password1";
		username2 = "username2";
		email2 = "email2";
		password2 = "password2";

		bytes = "bytes".getBytes();
		size = "size".getBytes().length;
		contentType = "contentType";
		filename = "filename";

		user1 = User.builder()
		  .id(UUID.randomUUID())
		  .username(username1)
		  .email(email1)
		  .password(password1)
		  .profileImage(null)
		  .build();

		userDto1 = UserDto.builder()
		  .id(user1.getId())
		  .username(user1.getUsername())
		  .email(user1.getEmail())
		  .profile(null)
		  .online(true)
		  .build();

		binaryContent = BinaryContent.builder()
		  .id(UUID.randomUUID())
		  .fileName(filename)
		  .contentType(contentType)
		  .size(size)
		  .build();
		binaryContentDto = BinaryContentDto.builder()
		  .id(binaryContent.getId())
		  .fileName(filename)
		  .size(size)
		  .contentType(contentType)
		  .build();

		user2 = User.builder()
		  .id(UUID.randomUUID())
		  .username(username2)
		  .email(email2)
		  .password(password2)
		  .profileImage(binaryContent)
		  .build();

		userDto2 = UserDto.builder()
		  .id(user2.getId())
		  .username(user2.getUsername())
		  .email(user2.getEmail())
		  .profile(binaryContentDto)
		  .online(true)
		  .build();

		channelId = UUID.randomUUID();
		publicChannelType = ChannelType.PUBLIC;
		privateChannelType = ChannelType.PRIVATE;
		channelName = "channelName";
		description = "description";
		lastMessageAt = Instant.now();

		participants = List.of(user1, user2);
		participantsDto = List.of(userDto1, userDto2);
	}

	@Test
	@DisplayName("공개 채널 생성 테스트")
	void createPublicChannel() throws Exception {
		ChannelDto channelDto = ChannelDto.builder()
		  .id(channelId)
		  .type(publicChannelType)
		  .name(channelName)
		  .description(description)
		  .lastMessageAt(lastMessageAt)
		  .build();

		ChannelResponse channelResponse = ChannelResponse.builder()
		  .id(channelDto.getId())
		  .type(channelDto.getType())
		  .name(channelDto.getName())
		  .description(channelDto.getDescription())
		  .participants(channelDto.getParticipants())
		  .lastMessageAt(channelDto.getLastMessageAt())
		  .build();

		CreatePublicChannelRequest request = CreatePublicChannelRequest.builder()
		  .name(channelName)
		  .description(description)
		  .build();

		given(channelService.createPublic(any())).willReturn(channelDto);
		given(channelMapper.toResponse(any())).willReturn(channelResponse);

		mockMvc.perform(post("/api/channels/public")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
		  ).andExpect(status().isCreated())
		  .andExpect(jsonPath("$.id").value(channelId.toString()))
		  .andExpect(jsonPath("$.type").value(publicChannelType.name()))
		  .andExpect(jsonPath("$.name").value(channelName))
		  .andExpect(jsonPath("$.description").value(description))

		  .andExpect(jsonPath("$.lastMessageAt").value(lastMessageAt.toString()))
		;

	}

	@Test
	@DisplayName("비공개 채널 생성 테스트")
	void createPrivateChannel() throws Exception {
		ChannelDto channelDto = ChannelDto.builder()
		  .id(channelId)
		  .type(privateChannelType)
		  .name(channelName)
		  .description(description)
		  .participants(participantsDto)
		  .lastMessageAt(lastMessageAt)
		  .build();

		ChannelResponse channelResponse = ChannelResponse.builder()
		  .id(channelDto.getId())
		  .type(channelDto.getType())
		  .name(channelDto.getName())
		  .description(channelDto.getDescription())
		  .participants(channelDto.getParticipants())
		  .lastMessageAt(channelDto.getLastMessageAt())
		  .build();

		CreatePrivateChannelRequest request = CreatePrivateChannelRequest.builder()
		  .participantIds(participants.stream().map(User::getId).toList())
		  .build();

		given(channelService.createPrivate(any())).willReturn(channelDto);
		given(channelMapper.toResponse(any())).willReturn(channelResponse);

		mockMvc.perform(post("/api/channels/private")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
		  )
		  .andExpect(jsonPath("$.id").value(channelId.toString()))
		  .andExpect(jsonPath("$.type").value(privateChannelType.name()))

		  .andExpect(jsonPath("$.participants[0].id").value(user1.getId().toString()))
		  .andExpect(jsonPath("$.participants[0].username").value(user1.getUsername()))
		  .andExpect(jsonPath("$.participants[0].email").value(user1.getEmail()))

		  .andExpect(jsonPath("$.participants[1].id").value(user2.getId().toString()))
		  .andExpect(jsonPath("$.participants[1].username").value(user2.getUsername()))
		  .andExpect(jsonPath("$.participants[1].email").value(user2.getEmail()))
		  .andExpect(jsonPath("$.participants[1].profile.id").value(binaryContent.getId().toString()))
		  .andExpect(jsonPath("$.participants[1].profile.fileName").value(filename))
		  .andExpect(jsonPath("$.participants[1].profile.size").value(size))
		  .andExpect(jsonPath("$.participants[1].profile.contentType").value(contentType))

		  .andExpect(jsonPath("$.lastMessageAt").value(lastMessageAt.toString()));
	}

	@Test
	@DisplayName("비공개 채널 생성 테스트 - 빈 사용자 목록이 주어졌을 때")
	void createPrivateChannelWithEmptyParticipants() throws Exception {
		CreatePrivateChannelRequest request = CreatePrivateChannelRequest.builder()
		  .participantIds(List.of()) // 빈 배열
		  .build();

		mockMvc.perform(post("/api/channels/private")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)))
		  .andExpect(status().isBadRequest())
		  .andExpect(jsonPath("$.code").value(VALIDATION_ERROR.name()))
		  .andExpect(jsonPath("$.message").value(VALIDATION_ERROR.getMessage()));
	}

	@Test
	void deleteChannel() {
	}

	@Test
	void updatePublicChannel() {
	}

	@Test
	void getAllByUserId() {
	}
}