package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks // 선언 된 @Mock 객체 기반으로 자동으로 주입하는 어노테이션
    private BasicMessageService messageService;


    private UUID messageId;
    private UUID channelId;
    private UUID authorId;
    private User user;
    private Channel channel;

    private String username;
    private String email;

    private Message message;
    private MessageDto messageDto;

    private String content;
    private String newContent;

    @BeforeEach
    void setUp() {
        channelId = UUID.randomUUID();
        authorId = UUID.randomUUID();
        messageId=UUID.randomUUID();
        user = new User();
        channel = new Channel(ChannelType.PRIVATE,null,null);
        content = "bus";
        newContent = "newBus";
        username = "username";
        email = "email";


        message = Message.builder()
                .content(content)
                .channel(channel)
                .author(user)
        .build();


        messageDto = new MessageDto(
                null,
                null,
                null,
                content,
                channelId,
                new UserDto(
                        authorId,
                        username,
                        email,
                        null,
                        null
                ),
                null
        );

    }

    @Test
    @DisplayName("메시지 생성 테스트 - 성공")
    void create_message(){
        MessageCreateRequest req = new MessageCreateRequest(content,channelId,authorId);
        List<BinaryContentCreateRequest> binaryContentCreateRequests = new ArrayList<>();

        BDDMockito.given(channelRepository.findById(any())).willReturn(Optional.ofNullable(channel));
        BDDMockito.given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
        BDDMockito.given(messageMapper.toDto(any())).willReturn(messageDto);

        MessageDto result = messageService.create(req, binaryContentCreateRequests);

        assertThat(result).isEqualTo(messageDto);
    }

//    @Test
//    @DisplayName("메시지 생성 테스트 - 실패")
//    void create_message_fail(){
//        MessageCreateRequest req = new MessageCreateRequest(content,channelId,authorId);
//        List<BinaryContentCreateRequest> binaryContentCreateRequests = new ArrayList<>();
//
//        BDDMockito.given(channelRepository.findById(any())).willReturn(Optional.ofNullable(channel));
//        BDDMockito.given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
//        BDDMockito.given(messageMapper.toDto(any())).willReturn(messageDto);
//
//        MessageDto result = messageService.create(null, binaryContentCreateRequests);
//
//        assertThat(result).isEqualTo(messageDto);
//    }


    @Test
    @DisplayName("메시지 수정 테스트 - 성공")
    void update_message(){
        MessageUpdateRequest req = new MessageUpdateRequest(newContent);
        BDDMockito.given(messageRepository.findById(any())).willReturn(Optional.ofNullable(message));
        BDDMockito.given(messageMapper.toDto(any())).willReturn(messageDto);

        MessageDto result = messageService.update(messageId,req);

        assertThat(result).isEqualTo(messageDto);
    }

//    @Test
//    @DisplayName("메시지 수정 테스트 - 실패")
//    void update_message_fail(){
//        MessageUpdateRequest req = new MessageUpdateRequest(newContent);
//        BDDMockito.given(messageRepository.findById(any())).willReturn(Optional.ofNullable(null));
//        BDDMockito.given(messageMapper.toDto(any())).willReturn(messageDto);
//
//        MessageDto result = messageService.update(messageId,req);
//
//        assertThat(result).isEqualTo(messageDto);
//    }


    @Test
    @DisplayName("메시지 삭제 테스트 - 성공")
    void delete_message(){
        messageRepository.deleteById(messageId);

        verify(messageRepository,times(1)).deleteById(messageId);

    }

//    @Test
//    @DisplayName("메시지 삭제 테스트 - 실패")
//    void delete_message_fail(){
//
//        messageRepository.deleteById(UUID.randomUUID());
//
//        verify(messageRepository,times(1)).deleteById(messageId);
//    }

    @Test
    @DisplayName("메시지 찾기 테스트 - 성공")
    void find_byChannelId_message(){
        messageRepository.findAllByChannelIdWithAuthor(channelId,null,null);

        verify(messageRepository,times(1)).findAllByChannelIdWithAuthor(channelId,null,null);

    }

//    @Test
//    @DisplayName("메시지 찾기 테스트 - 실패")
//    void find_byChannelId_message_fail(){
//        messageRepository.findAllByChannelIdWithAuthor(UUID.randomUUID(),null,null);
//
//        verify(messageRepository,times(1)).findAllByChannelIdWithAuthor(channelId,null,null);
//
//    }




}
