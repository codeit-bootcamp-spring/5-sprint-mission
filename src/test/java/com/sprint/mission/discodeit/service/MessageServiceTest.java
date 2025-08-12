package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.AddMessageRequest;
import com.sprint.mission.discodeit.dto.request.AddPublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.AddUserRequest;
import com.sprint.mission.discodeit.dto.request.UpdateMessageRequest;
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
        AddUserRequest addUserRequest = new AddUserRequest("UserName", "UserEmail", "PW", "PHONE", null);
        User user = userService.addUser(addUserRequest);
        AddPublicChannelRequest addPublicChannelRequest = new AddPublicChannelRequest("channelName", "channelDescription", user.getId());
        Channel channel = channelService.addPublicChannel(addPublicChannelRequest);
        byte[] image1 = {0x01, 0x02, 0x03, 0x04};
        byte[] image2 = {0x01, 0x02, 0x03, 0x04};
        BinaryContent binaryContent1 = new BinaryContent(image1);
        BinaryContent binaryContent2 = new BinaryContent(image2);
        AddMessageRequest messageContent = new AddMessageRequest("messageContent", user.getId(), channel.getId(), binaryContent1.getId(), binaryContent2.getId());
        Message message = messageService.addMessage(messageContent);
        byte[] image3 = {0x01, 0x02, 0x03, 0x04};
        BinaryContent binaryContent3 = new BinaryContent(image3);

        UpdateMessageRequest updatedMessageContent = new UpdateMessageRequest( "updatedMessageContent", binaryContent3.getId());
        messageService.updateMessage(message.getId(), updatedMessageContent);

        Message updatedMessage = messageService.getMessageById(message.getId());

        Assertions.assertThat(updatedMessage.getId()).isEqualTo(message.getId());
        Assertions.assertThat(updatedMessage.getContent()).isEqualTo(updatedMessageContent.messageContent());
        Assertions.assertThat(updatedMessage.getAttachmentIds()).containsExactly(binaryContent3.getId());
        Assertions.assertThat(updatedMessage.getAttachmentIds().size()).isEqualTo(1);
    }
}
