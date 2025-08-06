package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.AddPrivateChannelDto;
import com.sprint.mission.discodeit.dto.request.AddPublicChannelDto;
import com.sprint.mission.discodeit.dto.request.AddUserDto;
import com.sprint.mission.discodeit.dto.response.GetChannelByIdDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

        messageService.addMessage("publicMessage1", user1.getId(), publicChannel.getId());
        messageService.addMessage("publicMessage2", user1.getId(), publicChannel.getId());
        messageService.addMessage("privateMessage1", user2.getId(), privateChannel.getId());
        messageService.addMessage("privateMessage2", user2.getId(), privateChannel.getId());

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

}
