package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.dto.response.GetChannelByIdResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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
        user1 = userService.addUser(new AddUserRequest("testName1", "testMail1", "testPassword1", "testPhone1", null));
        user2 = userService.addUser(new AddUserRequest("testName2", "testMail2", "testPassword2", "testPhone1", null));
    }

    @Test
    public void addPublicChannelTest(){
        AddPublicChannelRequest addPublicChannelRequest = new AddPublicChannelRequest("testName1", "testDescription", user1.getId());
        Channel channel = channelService.addPublicChannel(addPublicChannelRequest);

        Assertions.assertThat(channel.getName()).isEqualTo(addPublicChannelRequest.channelName());
        Assertions.assertThat(channel.getChannelType()).isEqualTo(ChannelType.PUBLIC);
        Assertions.assertThat(channel.getOwnerUserId()).isEqualTo(addPublicChannelRequest.ownerUserId());
    }

    @Test
    public void addPrivateChannelTest(){
        AddPrivateChannelRequest addPrivateChannelRequest = new AddPrivateChannelRequest(user1.getId());
        Channel channel = channelService.addPrivateChannel(addPrivateChannelRequest);

        Assertions.assertThat(channel.getChannelType()).isEqualTo(ChannelType.PRIVATE);
        Assertions.assertThat(channel.getOwnerUserId()).isEqualTo(addPrivateChannelRequest.ownerUserId());
        Assertions.assertThat(channel.getName()).isNull();
        Assertions.assertThat(channel.getDescription()).isNull();
    }

    @Test
    public void GetChannelByIdTest(){
        Channel publicChannel = channelService.addPublicChannel(new AddPublicChannelRequest("testPublic", "testDescription", user1.getId()));
        Channel privateChannel = channelService.addPrivateChannel(new AddPrivateChannelRequest(user2.getId()));


        messageService.addMessage(new AddMessageRequest("publicMessage1", user1.getId(), publicChannel.getId()));
        messageService.addMessage(new AddMessageRequest("publicMessage2", user1.getId(), publicChannel.getId()));
        messageService.addMessage(new AddMessageRequest("privateMessage1", user2.getId(), privateChannel.getId()));
        messageService.addMessage(new AddMessageRequest("privateMessage12", user2.getId(), privateChannel.getId()));

        GetChannelByIdResponse channelById = channelService.getChannelById(publicChannel.getId());
        GetChannelByIdResponse channelById2 = channelService.getChannelById(privateChannel.getId());

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
        Channel publicChannel = channelService.addPublicChannel(new AddPublicChannelRequest("testPublic", "testDescription", user1.getId()));
        Channel privateChannel1 = channelService.addPrivateChannel(new AddPrivateChannelRequest(user1.getId()));
        Channel privateChannel2 = channelService.addPrivateChannel(new AddPrivateChannelRequest(user1.getId()));

        messageService.addMessage(new AddMessageRequest("publicMessage1", user1.getId(), publicChannel.getId()));
        messageService.addMessage(new AddMessageRequest("publicMessage2", user1.getId(), publicChannel.getId()));
        messageService.addMessage(new AddMessageRequest("privateMessage1", user2.getId(), privateChannel1.getId()));
        messageService.addMessage(new AddMessageRequest("privateMessage12", user2.getId(), privateChannel2.getId()));

        // when
        List<GetChannelByIdResponse> allChannelByUserId = channelService.getAllChannelByUserId(user1.getId());

        // Then
        Assertions.assertThat(allChannelByUserId.size()).isEqualTo(3);

        List<UUID> channelIds = allChannelByUserId.stream().map(GetChannelByIdResponse::channel).map(Channel::getId).toList();
        Assertions.assertThat(channelIds).contains(publicChannel.getId(), privateChannel1.getId(), privateChannel2.getId());
    }

    @Test
    public void updateChannelTest(){

        Channel publicChannel = channelService.addPublicChannel(new AddPublicChannelRequest("testPublic", "testDescription", user1.getId()));
        Channel privateChannel = channelService.addPrivateChannel(new AddPrivateChannelRequest(user1.getId()));

        UpdateChannelRequest updatePublicChannelDto = new UpdateChannelRequest("updateName", "updateDescription");

        channelService.updateChannel(updatePublicChannelDto, publicChannel.getId());
        GetChannelByIdResponse channelById = channelService.getChannelById(publicChannel.getId());

        Assertions.assertThat(channelById.channel().getName()).isEqualTo(updatePublicChannelDto.channelName());
        Assertions.assertThat(channelById.channel().getDescription()).isEqualTo(updatePublicChannelDto.channelDescription());
        Assertions.assertThat(channelById.channel().getId()).isEqualTo(publicChannel.getId());

        UpdateChannelRequest updatePrivateChannelDto = new UpdateChannelRequest("updateName", "updateDescription");

        Assertions.assertThatThrownBy(() -> channelService.updateChannel(updatePrivateChannelDto, privateChannel.getId())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void deleteChannelTest(){
        List<ReadStatus> all = readStatusRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(0);

        Channel privateChannel = channelService.addPrivateChannel(new AddPrivateChannelRequest(user1.getId()));
        messageService.addMessage(new AddMessageRequest("privateMessage1", user1.getId(), privateChannel.getId()));
        messageService.addMessage(new AddMessageRequest("privateMessage2", user1.getId(), privateChannel.getId()));
        messageService.addMessage(new AddMessageRequest("privateMessage3", user1.getId(), privateChannel.getId()));

        all = readStatusRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(1);

        List<Message> messagesBeforeDelete = messageService.getAllMessage();
        Assertions.assertThat(messagesBeforeDelete).hasSize(3);
        Assertions.assertThat(channelService.getChannelById(privateChannel.getId())).isNotNull();

        channelService.deleteChannel(privateChannel.getId());

        // 채널이 삭제되면 채널 내의 관련 메시지는 삭제되어야 한다.
        List<Message> messagesAfterDelete = messageService.getAllMessage();
        Assertions.assertThat(messagesAfterDelete).isEmpty();
        Assertions.assertThatThrownBy(() -> channelService.getChannelById(privateChannel.getId())).isInstanceOf(NoSuchElementException.class);

        // 채널이 삭제되면 관련 ReadStatus도 삭제되어야한다.
        all = readStatusRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(0);
        List<UUID> usersInChannel = readStatusRepository.findUsersIdByChannelId(privateChannel.getId());
        Assertions.assertThat(usersInChannel).isEmpty();

    }
}
