package com.sprint.mission.discodeit.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.bytebuddy.asm.MemberSubstitution.Substitution.Chain.Step.ForField.Read;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private BasicChannelService channelService;

    //테스트에 활용될 객체나 변수
    private ChannelDto createPublicChannelDto;
    private ChannelDto createPrivateChannelDto;
    private ChannelDto updateChannelDto;


    @BeforeEach
    void setUp(){
        createPublicChannelDto = new ChannelDto(
                UUID.randomUUID(),
                ChannelType.PUBLIC,
                "채널1",
        "채널1입니다.",
                null,null);
        updateChannelDto = new ChannelDto(
                UUID.randomUUID(),
                ChannelType.PUBLIC,
                "채널2",
                "채널2입니다.",
                null,null);
    }

    @Test
    void create_channel_public(){
        //given
        PublicChannelCreateRequest req = new PublicChannelCreateRequest("채널1","채널1입니다");
        String name = req.name();
        String description = req.description();
        Channel channel = new Channel(ChannelType.PUBLIC, name, description);


//        return channelMapper.toDto(channel);
        given(channelMapper.toDto(any())).willReturn(createPublicChannelDto);



        //when
        ChannelDto result = channelService.create(req);



        //then
        assertThat(result).isEqualTo(createPublicChannelDto);
        verify(channelRepository, times(1)).save(any());
    }

    @Test
    void create_channel_private(){
        //given
        List<UUID> participantIds = new ArrayList<>();
        participantIds.add(UUID.randomUUID());
        participantIds.add(UUID.randomUUID());
        List<User> fakeUsers = List.of(
                new User("user1", "user1@test.com", "1234", null),
                new User("user2", "user2@test.com", "1234", null)
        );

        PrivateChannelCreateRequest req =  new PrivateChannelCreateRequest(participantIds);
        given(channelMapper.toDto(any())).willReturn(createPrivateChannelDto);
        given(userRepository.findAllById(any())).willReturn(fakeUsers);

        //when
//        return channelMapper.toDto(channel);
        ChannelDto result = channelService.create(req);


        //then
        assertThat(result).isEqualTo(createPrivateChannelDto);
        verify(channelRepository, times(1)).save(any());
        verify(userRepository, times(1)).findAllById(any());
        verify(readStatusRepository, times(1)).saveAll(any());
    }

    @Test
    void update_channel(){
        //given
        Channel channel = new Channel(ChannelType.PUBLIC,"채널1","채널1입니다.");
        PublicChannelUpdateRequest req = new PublicChannelUpdateRequest("채널2","채널2입니다.");
        String newName = req.newName();
        String newDescription = req.newDescription();

//        Channel channel = channelRepository.findById(channelId)
        given(channelRepository.findById(any())).willReturn(Optional.of(channel));
        given(channelMapper.toDto(any())).willReturn(updateChannelDto);

        //when
        ChannelDto channelDto = channelService.update(channel.getId(), req);

        //then
        assertThat(channelDto).isEqualTo(updateChannelDto);
        verify(channelRepository, times(1)).findById(any());
    }

    @Test
    void delete_channel(){
        UUID channelId = UUID.randomUUID();
        given(channelRepository.existsById(any())).willReturn(true);

        channelService.delete(channelId);

        verify(messageRepository,times(1)).deleteAllByChannelId(any());
        verify(readStatusRepository,times(1)).deleteAllByChannelId(any());
        verify(channelRepository,times(1)).deleteById(any());

    }

    @Test
    void findByUserId_channel(){
        User user = new User("user1", "user1@test.com", "1234", null);
        Channel channel = new Channel(ChannelType.PUBLIC,"채널1","채널1입니다.");

        List<ReadStatus> readStatuses = new ArrayList<>();
        readStatuses.add(new ReadStatus(user,channel,null));

        List<ChannelDto> channelDtos = new ArrayList<>();
        channelDtos.add(new ChannelDto(channel.getId(), ChannelType.PUBLIC, "채널1", "채널1입니다.",null,null));

        given(readStatusRepository.findAllByUserId(any())).willReturn(readStatuses);
        given(channelRepository.findAllByTypeOrIdIn(any(),any())).willReturn(List.of(channel));
        //?
        given(channelMapper.toDto(channel)).willReturn(channelDtos.get(0));


        List<ChannelDto> result = channelService.findAllByUserId(user.getId());

        assertThat(result).isEqualTo(channelDtos);

    }




}
