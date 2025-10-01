package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceTest {
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChannelMapper channelMapper;

    @InjectMocks // 선언 된 @Mock 객체 기반으로 자동으로 주입하는 어노테이션
    private BasicChannelService channelService;

    private UUID channelId;

    private UUID userId;

    private ChannelType type;
    private ChannelType Ptype;
    private String name;
    private String description;

    private String newname;
    private String newdescription;

    private String username;
    private String email;
    private String password;

    private User user;
    private Channel channel;
    private Channel newChannel;
    private Channel privateChannel;
    private ChannelDto channelDto;
    private ChannelDto privateChannelDto;
    private ChannelDto newChannelDto;

    @BeforeEach
    void setUp(){
        channelId = UUID.randomUUID();
        userId = UUID.randomUUID();
        type = ChannelType.PUBLIC;
        Ptype = ChannelType.PRIVATE;
        name = "bus1";
        description = "bustam";

        newname = "bus2";
        newdescription = "bustajima";


        username = "test01";
        email = "test01@email.com";
        password = "password1234";

        user = User.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();

        channel = Channel.builder()
                .type(type)
                .name(name)
                .description(description)
                .build();

        channelDto = new ChannelDto(
                channelId,
                type,
                name,
                description,
                null,
                null
        );
        privateChannel = Channel.builder()
                .type(Ptype)
                .build();
        privateChannelDto = new ChannelDto(
                null,
                type,
                null,
                null,
                null,
                null
        );


        newChannelDto = new ChannelDto(
                channelId,
                type,
                newname,
                newdescription,
                null,
                null
        );
    }

    @Test
    @DisplayName("공개 채널 생성 테스트 - 성공")
    void create_public_channel(){
        // given
        PublicChannelCreateRequest req = new PublicChannelCreateRequest(name,description);

        // given 모의 설정(Mock 시나리오 설정)
        // User user = userMapper.toUser(newUser);
        given(channelMapper.toDto(any())).willReturn(channelDto);

        // userRepository.existsByUsername(user.getUsername())
        given(channelRepository.save(any())).willReturn(channel);


        // when
        ChannelDto result = channelService.create(req);
        // then
        assertThat(result).isEqualTo(channelDto);
    }

    @Test
    @DisplayName("비공개 채널 생성 테스트 - 성공")
    void create_private_channel(){
        // given
        PrivateChannelCreateRequest req = new PrivateChannelCreateRequest(new ArrayList<>());

        // given 모의 설정(Mock 시나리오 설정)
        // User user = userMapper.toUser(newUser);
        given(channelMapper.toDto(any())).willReturn(privateChannelDto);

        // userRepository.existsByUsername(user.getUsername())
        given(channelRepository.save(any())).willReturn(privateChannel);


        // when
        ChannelDto result = channelService.create(req);
        // then
        assertThat(result).isEqualTo(privateChannelDto);
    }

    @Test
    @DisplayName("채널 수정 테스트 - 성공")
    void update_channel(){
        // given
        PublicChannelUpdateRequest req = new PublicChannelUpdateRequest(newname,newdescription);

        // given 모의 설정(Mock 시나리오 설정)
        given(channelRepository.findById(channelId)).willReturn(Optional.ofNullable(channel));

        given(channelMapper.toDto(any())).willReturn(newChannelDto);

        // when
        ChannelDto result = channelService.update(channelId,req);

        assertThat(result).isEqualTo(newChannelDto);
    }

//    @Test
//    @DisplayName("채널 수정 테스트 - 실패")
//    void update_channel_fail(){
//        // given
//        PublicChannelUpdateRequest req = new PublicChannelUpdateRequest(newname,newdescription);
//
//        // given 모의 설정(Mock 시나리오 설정)
//        given(channelRepository.findById(channelId)).willReturn(Optional.ofNullable(null));
//
//        given(channelMapper.toDto(any())).willReturn(newChannelDto);
//
//        // when
//        ChannelDto result = channelService.update(channelId,req);
//
//        assertThat(result).isEqualTo(newChannelDto);
//    }


    @Test
    @DisplayName("채널 삭제 테스트 - 성공")
    void delete_channel(){

        // given 모의 설정(Mock 시나리오 설정)
        given(channelRepository.existsById(channelId)).willReturn(true);
        messageRepository.deleteAllByChannelId(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);

        channelService.delete(channelId);
        // then
        verify(channelRepository, times(1)).existsById(channelId);
    }


//    @Test
//    @DisplayName("채널 삭제 테스트 - 실패")
//    void delete_channel_fail(){
//
//        // given 모의 설정(Mock 시나리오 설정)
//        given(channelRepository.existsById(channelId)).willReturn(false);
//        messageRepository.deleteAllByChannelId(channelId);
//        readStatusRepository.deleteAllByChannelId(channelId);
//
//        channelService.delete(channelId);
//        // then
//        verify(channelRepository, times(1)).existsById(channelId);
//    }

    @Test
    @DisplayName("채널 찾기 테스트 - 성공")
    void find_byUserId_channel(){// 구독 채널 ID만 mocking

        ReadStatus readStatus = new ReadStatus(new User(), privateChannel, Instant.now());
        given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(readStatus));

        // findAllByTypeOrIdIn mocking
        List<Channel> channels = List.of(channel,privateChannel);
        given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList()))
                .willReturn(channels);
        // mapper mocking
        given(channelMapper.toDto(any())).willReturn(channelDto);

        // when
        List<ChannelDto> result = channelService.findAllByUserId(userId);

        // then
        assertThat(result).hasSize(2);

    }

//    @Test
//    @DisplayName("채널 찾기 테스트 - 실패")
//    void find_byUserId_channel_fail(){
//
//        ReadStatus readStatus = new ReadStatus(new User(), privateChannel, Instant.now());
//        given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(readStatus));
//
//        // findAllByTypeOrIdIn mocking
//        List<Channel> channels = List.of(channel,privateChannel);
//        given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList()))
//                .willReturn(channels);
//        // mapper mocking
//        given(channelMapper.toDto(any())).willReturn(channelDto);
//
//        // when
//        List<ChannelDto> result = channelService.findAllByUserId(UUID.randomUUID());
//
//        // then
//        assertThat(result).hasSize(2);
//
//    }


}
