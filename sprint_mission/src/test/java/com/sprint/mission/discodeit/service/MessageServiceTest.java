package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.MessageMapperImpl;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapperImpl;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserMapperImpl;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    UserMapper userMapper;

    @Spy
    @InjectMocks
    private MessageMapper messageMapper = new MessageMapperImpl(); // 실제 구현체 사용

    @InjectMocks
    private BasicMessageService messageService;
    @Mock
    PageResponseMapper pageResponseMapper;

    private Channel channel;
    private User author;

    @BeforeEach
    void setUp(){
        channel = new Channel(ChannelType.PUBLIC,"채널1","채널1입니다.");
        author = new User("test1","test1@test.com","1234",null);
    }


    @Test
    void create_message_without_binaryContent()
    {

        MessageCreateRequest req = new MessageCreateRequest("메세지내용", channel.getId(), author.getId());
        Message message = new Message(req.content(), channel,author,null);

        MessageDto createMessageDto = messageMapper.toDto(message);


        given(channelRepository.findById(any())).willReturn(Optional.of(channel));
        given(userRepository.findById(any())).willReturn(Optional.of(author));

        MessageDto result = messageService.create(req, new ArrayList<>());

        assertThat(result).isEqualTo(createMessageDto);

    }

    @Test
    void update_message_without_binaryContent()
    {
        MessageUpdateRequest req = new MessageUpdateRequest("수정된내용");
        Message message = new Message("수정된내용",channel,author,null);

        given(messageRepository.findById(any())).willReturn(Optional.of(message));

        MessageDto result = messageService.update(message.getId(),req);

        assertThat(result).isEqualTo(messageMapper.toDto(message));

    }

    @Test
    void delete_message()
    {

        given(messageRepository.existsById(any())).willReturn(true);

        messageService.delete(any());

        verify(messageRepository,times(1)).deleteById(any());

    }

    @Test
    void findAllByChannelId(){
        Message message1 = new Message("메세지1",channel,author,null);
        Message message2 = new Message("메세지2",channel,author,null);
        MessageDto messageDto1 = messageMapper.toDto(message1);
        MessageDto messageDto2 = messageMapper.toDto(message2);

        List<Message> content = List.of(message1,message2);
        Slice<Message> messages = new SliceImpl<>(content, Pageable.unpaged(), false);
        given(messageRepository.findAllByChannelIdWithAuthor(any(),any(),any())).willReturn(messages);
        given(userMapper.toDto(any())).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new UserDto(user.getId(), user.getUsername(), user.getEmail(), null, true);
        });
        given(pageResponseMapper.fromSlice(any(Slice.class), any()))
                .willAnswer(invocation -> {
                    Slice<MessageDto> slice = invocation.getArgument(0);
                    Object nextCursor = invocation.getArgument(1);
                    return new PageResponse<>(slice.getContent(), (Instant) nextCursor,0,false,null);
                });

        PageResponse<MessageDto> result = messageService.findAllByChannelId(null, Instant.now(),null);


//        assertThat(result.nextCursor()).isNotNull();
        assertThat(result.content().get(0).content()).isEqualTo("메세지1");
        assertThat(result.content().get(1).content()).isEqualTo("메세지2");

    }

}
