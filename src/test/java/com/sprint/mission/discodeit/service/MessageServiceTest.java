package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.AddMessageDto;
import com.sprint.mission.discodeit.dto.request.AddPublicChannelDto;
import com.sprint.mission.discodeit.dto.request.AddUserDto;
import com.sprint.mission.discodeit.dto.request.UpdateMessageDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MessageServiceTest {

    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    ChannelService channelService;
    @Autowired
    BinaryContentRepository binaryContentRepository;


    @Test
    public void updateMessageTest(){
        AddUserDto addUserDto = new AddUserDto("UserName", "UserEmail", "PW", "PHONE", null);
        User user = userService.addUser(addUserDto);
        AddPublicChannelDto addPublicChannelDto = new AddPublicChannelDto("channelName", "channelDescription", user.getId());
        Channel channel = channelService.addPublicChannel(addPublicChannelDto);
        byte[] image1 = {0x01, 0x02, 0x03, 0x04};
        byte[] image2 = {0x01, 0x02, 0x03, 0x04};
        BinaryContent binaryContent1 = new BinaryContent(image1);
        BinaryContent binaryContent2 = new BinaryContent(image2);
        AddMessageDto messageContent = new AddMessageDto("messageContent", user.getId(), channel.getId(), binaryContent1.getId(), binaryContent2.getId());
        Message message = messageService.addMessage(messageContent);
        byte[] image3 = {0x01, 0x02, 0x03, 0x04};
        BinaryContent binaryContent3 = new BinaryContent(image3);

        UpdateMessageDto updatedMessageContent = new UpdateMessageDto(message.getId(), "updatedMessageContent", binaryContent3.getId());
        messageService.updateMessage(updatedMessageContent);

        Message updatedMessage = messageService.getMessageById(message.getId());

        Assertions.assertThat(updatedMessage.getId()).isEqualTo(message.getId());
        Assertions.assertThat(updatedMessage.getContent()).isEqualTo(updatedMessageContent.messageContent());
        Assertions.assertThat(updatedMessage.getAttachmentIds()).containsExactly(binaryContent3.getId());
        Assertions.assertThat(updatedMessage.getAttachmentIds().size()).isEqualTo(1);
    }
}
