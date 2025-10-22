package com.sprint.mission.integration;

import static com.sprint.mission.discodeit.domain.enums.ChannelType.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.domain.dto.CreatePrivateChannelDTO;
import com.sprint.mission.discodeit.domain.dto.CreatePublicChannelDTO;
import com.sprint.mission.discodeit.domain.dto.CreateUserDTO;
import com.sprint.mission.discodeit.domain.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.domain.dto.user.UserDto;
import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.domain.request.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.domain.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.domain.request.UpdatePublicChannelRequest;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ChannelApiTest {

	@Autowired
	private BasicChannelService basicChannelService;
	@Autowired
	private BasicUserService basicUserService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ChannelRepository channelRepository;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockitoBean
	private S3Client s3Client;
	@MockitoBean
	private S3Presigner s3Presigner;

	String channelName;
	String description;

	Channel savedPublicChannel;
	ChannelDto publicChannelDto;
	Channel savedPrivateChannel1;
	ChannelDto privateChannelDto1;
	Channel savedPrivateChannelOnlyUser2;
	ChannelDto privateChannelOnlyUser2Dto;

	User savedUser1;
	User savedUser2;
	UserDto savedUser1Dto;
	UserDto savedUser2Dto;

	@BeforeEach
	void setUp() {
		savedUser1Dto = basicUserService.create(CreateUserDTO.builder()
		  .username("user1")
		  .email("user1")
		  .password("user1")
		  .binaryContent(null)
		  .build());
		savedUser1 = userRepository.findById(savedUser1Dto.getId()).get();
		savedUser2Dto = basicUserService.create(CreateUserDTO.builder()
		  .username("user2")
		  .email("user2")
		  .password("user2")
		  .binaryContent(null)
		  .build());
		savedUser2 = userRepository.findById(savedUser2Dto.getId()).get();

		channelName = "channelName";
		description = "description";

		publicChannelDto = basicChannelService.createPublic(CreatePublicChannelDTO.builder()
		  .name(channelName)
		  .description(description)
		  .build());
		savedPublicChannel = channelRepository.findById(publicChannelDto.getId()).get();

		privateChannelDto1 = basicChannelService.createPrivate(CreatePrivateChannelDTO.builder()
		  .userIds(List.of(savedUser1.getId(), savedUser2.getId()))
		  .build());
		savedPrivateChannel1 = channelRepository.findById(privateChannelDto1.getId()).get();

		privateChannelOnlyUser2Dto = basicChannelService.createPrivate(CreatePrivateChannelDTO.builder()
		  .userIds(List.of(savedUser2.getId()))
		  .build());
		savedPrivateChannelOnlyUser2 = channelRepository.findById(privateChannelOnlyUser2Dto.getId()).get();

	}

	@Test
	@DisplayName("공개채널 생성 API 테스트 ")
	public void createPublicChannelApiTest() throws Exception {
		CreatePublicChannelRequest request = CreatePublicChannelRequest.builder()
		  .name(channelName)
		  .description(description)
		  .build();

		mockMvc.perform(post("/api/channels/public")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
		  )
		  .andExpect(status().isCreated())
		  .andExpect(jsonPath("$.id", notNullValue()))
		  .andExpect(jsonPath("$.type").value(PUBLIC.name()))
		  .andExpect(jsonPath("$.name").value(channelName))
		  .andExpect(jsonPath("$.description").value(description))
		  .andExpect(jsonPath("$.participants.length()").value(0));

	}

	@Test
	@DisplayName("비공개채널 생성 API 테스트 ")
	public void createPrivateChannelApiTest() throws Exception {
		CreatePrivateChannelRequest request = CreatePrivateChannelRequest.builder()
		  .participantIds(List.of(savedUser1.getId(), savedUser2.getId()))
		  .build();

		mockMvc.perform(post("/api/channels/private")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
		  )
		  .andExpect(status().isCreated())
		  .andExpect(jsonPath("$.id", notNullValue()))
		  .andExpect(jsonPath("$.type").value(PRIVATE.name()))
		  .andExpect(jsonPath("$.name", nullValue()))
		  .andExpect(jsonPath("$.description", nullValue()))
		  .andExpect(jsonPath("$.participants.length()").value(2))
		  .andExpect(jsonPath("$.participants[*].id", containsInAnyOrder(
			savedUser1.getId().toString(),
			savedUser2.getId().toString()
		  )))
		  .andExpect(jsonPath("$.participants[*].username", containsInAnyOrder(
			savedUser1.getUsername(),
			savedUser2.getUsername()
		  )))
		  .andExpect(jsonPath("$.participants[*].username", containsInAnyOrder(
			savedUser1.getEmail(),
			savedUser2.getEmail()
		  )))

		;
	}

	@Test
	@DisplayName("채널 삭제 API 테스트 ")
	public void deleteChannelApiTest() throws Exception {

		UUID id = savedPublicChannel.getId();

		mockMvc.perform(delete("/api/channels/" + id)
		).andExpect(status().isNoContent());

	}

	@Test
	@DisplayName("채널 수정 API 테스트 ")
	public void updateChannelApiTest() throws Exception {
		UUID id = savedPublicChannel.getId();
		String newName = "newName";
		String newDescription = "newDescription";

		UpdatePublicChannelRequest request = UpdatePublicChannelRequest.builder()
		  .newName(newName)
		  .newDescription(newDescription)
		  .build();

		mockMvc.perform(patch("/api/channels/" + id)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request))
		  )
		  .andExpect(status().isOk())
		  .andExpect(jsonPath("$.id", notNullValue()))
		  .andExpect(jsonPath("$.type").value(PUBLIC.name()))
		  .andExpect(jsonPath("$.name").value(newName))
		  .andExpect(jsonPath("$.description").value(newDescription))
		  .andExpect(jsonPath("$.participants.length()").value(0));

	}

	@Test
	@DisplayName("채널 목록 조회 API 테스트 ")
	public void getChannelListApiTest() throws Exception {
		UUID id = savedUser2.getId();

		mockMvc.perform(get("/api/channels")
			.param("userId", id.toString())
		  )
		  .andExpect(status().isOk())
		  .andExpect(jsonPath("$.length()").value(3))
		  .andExpect(jsonPath("$.[*].id", containsInAnyOrder(
			savedPublicChannel.getId().toString(),
			savedPrivateChannel1.getId().toString(),
			savedPrivateChannelOnlyUser2.getId().toString()
		  )))
		  .andExpect(jsonPath("$.[*].type", containsInAnyOrder(
			PUBLIC.name(), PRIVATE.name(), PRIVATE.name()
		  )))
		  .andExpect(jsonPath("$.[*].name", containsInAnyOrder(
			savedPublicChannel.getName(),
			savedPrivateChannel1.getName(),
			savedPrivateChannelOnlyUser2.getName())
		  ))
		  .andExpect(jsonPath("$.[*].description", containsInAnyOrder(
			savedPublicChannel.getDescription(),
			savedPrivateChannel1.getDescription(),
			savedPrivateChannelOnlyUser2.getDescription())
		  ))
		  .andExpect(jsonPath("$.[*].participants.length()", containsInAnyOrder(
			0, 1, 2
		  )));

	}

}
