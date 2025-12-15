package com.sprint.mission.discodeit.channel.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.channel.application.ChannelService;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.channel.domain.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.channel.domain.exception.DuplicateChannelException;
import com.sprint.mission.discodeit.channel.domain.exception.ParticipantsNotFoundException;
import com.sprint.mission.discodeit.channel.domain.exception.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.channel.presentation.dto.ChannelDto;
import com.sprint.mission.discodeit.channel.presentation.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.global.error.GlobalExceptionHandler;
import com.sprint.mission.discodeit.global.security.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.global.security.jwt.JwtLogoutHandler;
import com.sprint.mission.discodeit.global.security.ratelimit.LoginRateLimitFilter;
import com.sprint.mission.discodeit.global.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.global.security.userdetails.dto.UserDetailsDto;
import com.sprint.mission.discodeit.support.TestSecurityConfig;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = ChannelController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {
            JwtAuthenticationFilter.class,
            LoginRateLimitFilter.class,
            JwtLogoutHandler.class
        }
    )
)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@DisplayName("ChannelController 테스트")
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    private static final UUID TEST_CHANNEL_ID = UUID.randomUUID();
    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final UUID TEST_USER_ID_2 = UUID.randomUUID();
    private static final String TEST_CHANNEL_NAME = "test-channel";
    private static final String TEST_CHANNEL_DESCRIPTION = "Test channel description";

    private DiscodeitUserDetails createUserDetails() {
        UserDetailsDto userDetailsDto = new UserDetailsDto(TEST_USER_ID, "testuser", Role.USER);
        return new DiscodeitUserDetails(userDetailsDto, "password");
    }

    private ChannelDto createPublicChannelDto() {
        return new ChannelDto(
            TEST_CHANNEL_ID,
            ChannelType.PUBLIC,
            TEST_CHANNEL_NAME,
            TEST_CHANNEL_DESCRIPTION,
            List.of(),
            null
        );
    }

    private ChannelDto createPrivateChannelDto() {
        UserDto user1 = new UserDto(TEST_USER_ID, "user1", "user1@test.com", null, true, Role.USER);
        UserDto user2 = new UserDto(TEST_USER_ID_2, "user2", "user2@test.com", null, false, Role.USER);
        return new ChannelDto(
            TEST_CHANNEL_ID,
            ChannelType.PRIVATE,
            null,
            null,
            List.of(user1, user2),
            null
        );
    }

    @Nested
    @DisplayName("POST /api/channels/public")
    class CreatePublic {

        @Test
        @WithMockUser(roles = "CHANNEL_MANAGER")
        @DisplayName("CHANNEL_MANAGER 권한으로 공개 채널 생성 시 201 반환")
        void createPublic_withChannelManagerRole_returns201() throws Exception {
            // given
            PublicChannelCreateRequest request = new PublicChannelCreateRequest(
                TEST_CHANNEL_NAME, TEST_CHANNEL_DESCRIPTION
            );
            ChannelDto channelDto = createPublicChannelDto();

            given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(channelDto);

            // when & then
            mockMvc.perform(post("/api/channels/public")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TEST_CHANNEL_ID.toString()))
                .andExpect(jsonPath("$.type").value("PUBLIC"))
                .andExpect(jsonPath("$.name").value(TEST_CHANNEL_NAME))
                .andExpect(jsonPath("$.description").value(TEST_CHANNEL_DESCRIPTION));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER 권한으로 공개 채널 생성 시 403 반환")
        void createPublic_withUserRole_returns403() throws Exception {
            // given
            PublicChannelCreateRequest request = new PublicChannelCreateRequest(
                TEST_CHANNEL_NAME, TEST_CHANNEL_DESCRIPTION
            );

            given(channelService.create(any(PublicChannelCreateRequest.class)))
                .willThrow(new AuthorizationDeniedException(
                    "Access Denied", new AuthorizationDecision(false)
                ));

            // when & then
            mockMvc.perform(post("/api/channels/public")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("인증 없이 공개 채널 생성 시 403 반환")
        void createPublic_withoutAuth_returns403() throws Exception {
            // given
            PublicChannelCreateRequest request = new PublicChannelCreateRequest(
                TEST_CHANNEL_NAME, TEST_CHANNEL_DESCRIPTION
            );

            // when & then
            mockMvc.perform(post("/api/channels/public")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "CHANNEL_MANAGER")
        @DisplayName("채널명 없이 생성 시 400 반환")
        void createPublic_withoutName_returns400() throws Exception {
            // given
            PublicChannelCreateRequest request = new PublicChannelCreateRequest(null, TEST_CHANNEL_DESCRIPTION);

            // when & then
            mockMvc.perform(post("/api/channels/public")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "CHANNEL_MANAGER")
        @DisplayName("빈 채널명으로 생성 시 400 반환")
        void createPublic_withBlankName_returns400() throws Exception {
            // given
            PublicChannelCreateRequest request = new PublicChannelCreateRequest("   ", TEST_CHANNEL_DESCRIPTION);

            // when & then
            mockMvc.perform(post("/api/channels/public")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/channels/private")
    class CreatePrivate {

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("비공개 채널 생성 시 201 반환")
        void createPrivate_withValidRequest_returns201() throws Exception {
            // given
            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
                Set.of(TEST_USER_ID, TEST_USER_ID_2)
            );
            ChannelDto channelDto = createPrivateChannelDto();

            given(channelService.create(any(PrivateChannelCreateRequest.class))).willReturn(channelDto);

            // when & then
            mockMvc.perform(post("/api/channels/private")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TEST_CHANNEL_ID.toString()))
                .andExpect(jsonPath("$.type").value("PRIVATE"))
                .andExpect(jsonPath("$.name").doesNotExist())
                .andExpect(jsonPath("$.participants").isArray())
                .andExpect(jsonPath("$.participants.length()").value(2));
        }

        @Test
        @DisplayName("인증 없이 비공개 채널 생성 시 403 반환")
        void createPrivate_withoutAuth_returns403() throws Exception {
            // given
            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
                Set.of(TEST_USER_ID, TEST_USER_ID_2)
            );

            // when & then
            mockMvc.perform(post("/api/channels/private")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("참가자가 1명일 때 400 반환")
        void createPrivate_withSingleParticipant_returns400() throws Exception {
            // given
            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(Set.of(TEST_USER_ID));

            // when & then
            mockMvc.perform(post("/api/channels/private")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("존재하지 않는 참가자로 생성 시 404 반환")
        void createPrivate_withNonExistingParticipants_returns404() throws Exception {
            // given
            UUID nonExistingId = UUID.randomUUID();
            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
                Set.of(TEST_USER_ID, nonExistingId)
            );

            given(channelService.create(any(PrivateChannelCreateRequest.class)))
                .willThrow(new ParticipantsNotFoundException(Set.of(nonExistingId)));

            // when & then
            mockMvc.perform(post("/api/channels/private")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("이미 존재하는 비공개 채널 생성 시 409 반환")
        void createPrivate_withDuplicateChannel_returns409() throws Exception {
            // given
            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
                Set.of(TEST_USER_ID, TEST_USER_ID_2)
            );

            given(channelService.create(any(PrivateChannelCreateRequest.class)))
                .willThrow(new DuplicateChannelException(TEST_USER_ID, TEST_USER_ID_2));

            // when & then
            mockMvc.perform(post("/api/channels/private")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /api/channels")
    class FindAll {

        @Test
        @DisplayName("인증된 사용자로 채널 목록 조회 시 200 반환")
        void findAll_withAuth_returns200() throws Exception {
            // given
            DiscodeitUserDetails userDetails = createUserDetails();
            ChannelDto publicChannel = createPublicChannelDto();
            ChannelDto privateChannel = createPrivateChannelDto();

            given(channelService.findAll(TEST_USER_ID)).willReturn(List.of(publicChannel, privateChannel));

            // when & then
            mockMvc.perform(get("/api/channels")
                    .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].type").value("PUBLIC"))
                .andExpect(jsonPath("$[1].type").value("PRIVATE"));
        }

        @Test
        @DisplayName("인증 없이 채널 목록 조회 시 403 반환")
        void findAll_withoutAuth_returns403() throws Exception {
            // when & then
            mockMvc.perform(get("/api/channels"))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("채널이 없을 때 빈 배열 반환")
        void findAll_withNoChannels_returnsEmptyArray() throws Exception {
            // given
            DiscodeitUserDetails userDetails = createUserDetails();

            given(channelService.findAll(TEST_USER_ID)).willReturn(List.of());

            // when & then
            mockMvc.perform(get("/api/channels")
                    .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("PATCH /api/channels/{channelId}")
    class Update {

        @Test
        @WithMockUser(roles = "CHANNEL_MANAGER")
        @DisplayName("CHANNEL_MANAGER 권한으로 채널 수정 시 200 반환")
        void update_withChannelManagerRole_returns200() throws Exception {
            // given
            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
                "new-name", "new description"
            );
            ChannelDto updatedChannel = new ChannelDto(
                TEST_CHANNEL_ID,
                ChannelType.PUBLIC,
                "new-name",
                "new description",
                List.of(),
                Instant.now()
            );

            given(channelService.update(eq(TEST_CHANNEL_ID), any(PublicChannelUpdateRequest.class)))
                .willReturn(updatedChannel);

            // when & then
            mockMvc.perform(patch("/api/channels/{channelId}", TEST_CHANNEL_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_CHANNEL_ID.toString()))
                .andExpect(jsonPath("$.name").value("new-name"))
                .andExpect(jsonPath("$.description").value("new description"));
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER 권한으로 채널 수정 시 403 반환")
        void update_withUserRole_returns403() throws Exception {
            // given
            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
                "new-name", "new description"
            );

            given(channelService.update(eq(TEST_CHANNEL_ID), any(PublicChannelUpdateRequest.class)))
                .willThrow(new AuthorizationDeniedException(
                    "Access Denied", new AuthorizationDecision(false)
                ));

            // when & then
            mockMvc.perform(patch("/api/channels/{channelId}", TEST_CHANNEL_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("인증 없이 채널 수정 시 403 반환")
        void update_withoutAuth_returns403() throws Exception {
            // given
            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
                "new-name", "new description"
            );

            // when & then
            mockMvc.perform(patch("/api/channels/{channelId}", TEST_CHANNEL_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "CHANNEL_MANAGER")
        @DisplayName("존재하지 않는 채널 수정 시 404 반환")
        void update_withNonExistingChannel_returns404() throws Exception {
            // given
            UUID nonExistingId = UUID.randomUUID();
            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
                "new-name", "new description"
            );

            given(channelService.update(eq(nonExistingId), any(PublicChannelUpdateRequest.class)))
                .willThrow(new ChannelNotFoundException(nonExistingId));

            // when & then
            mockMvc.perform(patch("/api/channels/{channelId}", nonExistingId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "CHANNEL_MANAGER")
        @DisplayName("비공개 채널 수정 시 403 반환")
        void update_privateChannel_returns403() throws Exception {
            // given
            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
                "new-name", "new description"
            );

            given(channelService.update(eq(TEST_CHANNEL_ID), any(PublicChannelUpdateRequest.class)))
                .willThrow(new PrivateChannelUpdateException());

            // when & then
            mockMvc.perform(patch("/api/channels/{channelId}", TEST_CHANNEL_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("DELETE /api/channels/{channelId}")
    class DeleteById {

        @Test
        @WithMockUser(roles = "CHANNEL_MANAGER")
        @DisplayName("CHANNEL_MANAGER 권한으로 채널 삭제 시 204 반환")
        void deleteById_withChannelManagerRole_returns204() throws Exception {
            // given
            willDoNothing().given(channelService).deleteById(TEST_CHANNEL_ID);

            // when & then
            mockMvc.perform(delete("/api/channels/{channelId}", TEST_CHANNEL_ID))
                .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("USER 권한으로 채널 삭제 시 403 반환")
        void deleteById_withUserRole_returns403() throws Exception {
            // given
            willThrow(new AuthorizationDeniedException(
                "Access Denied", new AuthorizationDecision(false)
            )).given(channelService).deleteById(TEST_CHANNEL_ID);

            // when & then
            mockMvc.perform(delete("/api/channels/{channelId}", TEST_CHANNEL_ID))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("인증 없이 채널 삭제 시 403 반환")
        void deleteById_withoutAuth_returns403() throws Exception {
            // when & then
            mockMvc.perform(delete("/api/channels/{channelId}", TEST_CHANNEL_ID))
                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "CHANNEL_MANAGER")
        @DisplayName("존재하지 않는 채널 삭제 시 404 반환")
        void deleteById_withNonExistingChannel_returns404() throws Exception {
            // given
            UUID nonExistingId = UUID.randomUUID();

            willThrow(new ChannelNotFoundException(nonExistingId))
                .given(channelService).deleteById(nonExistingId);

            // when & then
            mockMvc.perform(delete("/api/channels/{channelId}", nonExistingId))
                .andExpect(status().isNotFound());
        }
    }
}
