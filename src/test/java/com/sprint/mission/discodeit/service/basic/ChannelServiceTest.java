package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.dto.response.GetChannelByIdDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@SpringBootTest
public class ChannelServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ReadStatusService readStatusService;
    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ReadStatusRepository readStatusRepository;

    private User user1;
    private User user2;


    @BeforeEach
    public void setUp(){
        channelService.deleteAllChannel();
        readStatusService.deleteAllReadStatus();
        messageService.deleteAllMessage();
        userService.deleteAllUser();
        user1 = userService.addUser(new AddUserDto("testName1", "testMail1", "testPassword1", "testPhone1", null));
        user2 = userService.addUser(new AddUserDto("testName2", "testMail2", "testPassword2", "testPhone1", null));
    }

    @Test
    public void addPublicChannelTest(){
        AddPublicChannelDto addPublicChannelDto = new AddPublicChannelDto("testName1", "testDescription", user1.getId());
        Channel channel = channelService.addPublicChannel(addPublicChannelDto);

        Assertions.assertThat(channel.getName()).isEqualTo(addPublicChannelDto.channelName());
        Assertions.assertThat(channel.getChannelType()).isEqualTo(ChannelType.PUBLIC);
        Assertions.assertThat(channel.getOwnerUserId()).isEqualTo(addPublicChannelDto.ownerUserId());
    }

    @Test
    public void addPrivateChannelTest(){
        AddPrivateChannelDto addPrivateChannelDto = new AddPrivateChannelDto(user1.getId());
        Channel channel = channelService.addPrivateChannel(addPrivateChannelDto);

        Assertions.assertThat(channel.getChannelType()).isEqualTo(ChannelType.PRIVATE);
        Assertions.assertThat(channel.getOwnerUserId()).isEqualTo(addPrivateChannelDto.ownerUserId());
        Assertions.assertThat(channel.getName()).isNull();
        Assertions.assertThat(channel.getDescription()).isNull();
    }

    @Test
    public void GetChannelByIdTest(){
        Channel publicChannel = channelService.addPublicChannel(new AddPublicChannelDto("testPublic", "testDescription", user1.getId()));
        Channel privateChannel = channelService.addPrivateChannel(new AddPrivateChannelDto(user2.getId()));


        messageService.addMessage(new AddMessageDto("publicMessage1", user1.getId(), publicChannel.getId()));
        messageService.addMessage(new AddMessageDto("publicMessage2", user1.getId(), publicChannel.getId()));
        messageService.addMessage(new AddMessageDto("privateMessage1", user2.getId(), privateChannel.getId()));
        messageService.addMessage(new AddMessageDto("privateMessage12", user2.getId(), privateChannel.getId()));

        GetChannelByIdDto channelById = channelService.getChannelById(publicChannel.getId());
        GetChannelByIdDto channelById2 = channelService.getChannelById(privateChannel.getId());

        // PublicChannel
        Assertions.assertThat(channelById.channel().getId()).isEqualTo(publicChannel.getId());
        Assertions.assertThat(channelById.existingUsersIds()).isNull();
        Assertions.assertThat(channelById.recentMessageTime()).isNotNull();

        // PrivateChannel
        Assertions.assertThat(channelById2.channel().getId()).isEqualTo(privateChannel.getId());
        Assertions.assertThat(channelById2.existingUsersIds().size()).isEqualTo(1);
        Assertions.assertThat(channelById.recentMessageTime()).isNotNull();
    }

    @Test
    public void getAllChannelByUserIdTest(){

        // given
        Channel publicChannel = channelService.addPublicChannel(new AddPublicChannelDto("testPublic", "testDescription", user1.getId()));
        Channel privateChannel1 = channelService.addPrivateChannel(new AddPrivateChannelDto(user1.getId()));
        Channel privateChannel2 = channelService.addPrivateChannel(new AddPrivateChannelDto(user1.getId()));

        messageService.addMessage(new AddMessageDto("publicMessage1", user1.getId(), publicChannel.getId()));
        messageService.addMessage(new AddMessageDto("publicMessage2", user1.getId(), publicChannel.getId()));
        messageService.addMessage(new AddMessageDto("privateMessage1", user2.getId(), privateChannel1.getId()));
        messageService.addMessage(new AddMessageDto("privateMessage12", user2.getId(), privateChannel2.getId()));

        // when
        List<GetChannelByIdDto> allChannelByUserId = channelService.getAllChannelByUserId(user1.getId());

        // Then
        Assertions.assertThat(allChannelByUserId.size()).isEqualTo(3);

        List<UUID> channelIds = allChannelByUserId.stream().map(GetChannelByIdDto::channel).map(Channel::getId).toList();
        Assertions.assertThat(channelIds).contains(publicChannel.getId(), privateChannel1.getId(), privateChannel2.getId());
    }

    @Test
    public void updateChannelTest(){

        Channel publicChannel = channelService.addPublicChannel(new AddPublicChannelDto("testPublic", "testDescription", user1.getId()));
        Channel privateChannel = channelService.addPrivateChannel(new AddPrivateChannelDto(user1.getId()));

        UpdateChannelDto updatePublicChannelDto = new UpdateChannelDto(publicChannel.getId(),"updateName", "updateDescription");

        channelService.updateChannel(updatePublicChannelDto);
        GetChannelByIdDto channelById = channelService.getChannelById(publicChannel.getId());

        Assertions.assertThat(channelById.channel().getName()).isEqualTo(updatePublicChannelDto.channelName());
        Assertions.assertThat(channelById.channel().getDescription()).isEqualTo(updatePublicChannelDto.channelDescription());
        Assertions.assertThat(channelById.channel().getId()).isEqualTo(publicChannel.getId());

        UpdateChannelDto updatePrivateChannelDto = new UpdateChannelDto(privateChannel.getId(),"updateName", "updateDescription");

        Assertions.assertThatThrownBy(() -> channelService.updateChannel(updatePrivateChannelDto)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void deleteChannelTest(){
        Channel privateChannel = channelService.addPrivateChannel(new AddPrivateChannelDto(user1.getId()));
        messageService.addMessage(new AddMessageDto("privateMessage1", user1.getId(), privateChannel.getId()));
        messageService.addMessage(new AddMessageDto("privateMessage2", user1.getId(), privateChannel.getId()));
        messageService.addMessage(new AddMessageDto("privateMessage3", user1.getId(), privateChannel.getId()));

        List<Message> messagesBeforeDelete = messageService.getAllMessage();
        Assertions.assertThat(messagesBeforeDelete).hasSize(3);
        Assertions.assertThat(channelService.getChannelById(privateChannel.getId())).isNotNull();

        channelService.deleteChannel(privateChannel.getId());

        List<Message> messagesAfterDelete = messageService.getAllMessage();
        Assertions.assertThat(messagesAfterDelete).isEmpty();
        Assertions.assertThatThrownBy(() -> channelService.getChannelById(privateChannel.getId())).isInstanceOf(NoSuchElementException.class);
        List<UUID> usersInChannel = readStatusRepository.findUsersIdByChannelId(privateChannel.getId());
        Assertions.assertThat(usersInChannel).isEmpty();

    }
}
