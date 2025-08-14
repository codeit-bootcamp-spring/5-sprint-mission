package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.AddPrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.AddPublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.AddReadStatusRequest;
import com.sprint.mission.discodeit.dto.request.AddUserRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ReadStatusServiceTest {

    @Autowired
    private ReadStatusService readStatusService;
    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelService channelService;
    @Autowired
    private ChannelRepository channelRepository;

    @BeforeEach
    public void setUp() {
        readStatusService.deleteAllReadStatus();
        userService.deleteAllUser();
        channelService.deleteAllChannel();
    }

    @Test
    public void addReadStatusTest() {
        AddUserRequest addUserRequest = new AddUserRequest("testName", "testMail", "testPassword", "testPhone", null);
        User user = userService.addUser(addUserRequest);

        Assertions.assertThat(userRepository.findAll().size()).isEqualTo(1);
        AddPublicChannelRequest addPublicChannelRequest1 = new AddPublicChannelRequest("testChannelName", "testChannelDesc", user.getId());
        Channel channel = channelService.addPublicChannel(addPublicChannelRequest1);
        Assertions.assertThat(channelRepository.findAll().size()).isEqualTo(1);

        AddReadStatusRequest addReadStatusRequest = new AddReadStatusRequest(channel.getId(), user.getId());
        ReadStatus readStatus = readStatusService.addReadStatus(addReadStatusRequest);

        Assertions.assertThat(readStatus).isNotNull();
        Assertions.assertThat(readStatus.getUserId()).isEqualTo(addReadStatusRequest.userId());
        Assertions.assertThat(readStatus.getChannelId()).isEqualTo(addReadStatusRequest.channelId());
    }

    @Test
    public void getReadStatusTest() {
        AddUserRequest addUserRequest = new AddUserRequest("testName", "testMail", "testPassword", "testPhone", null);
        User user = userService.addUser(addUserRequest);

        Assertions.assertThat(userRepository.findAll().size()).isEqualTo(1);
        AddPublicChannelRequest addPublicChannelRequest1 = new AddPublicChannelRequest("testChannelName", "testChannelDesc", user.getId());
        Channel channel = channelService.addPublicChannel(addPublicChannelRequest1);
        Assertions.assertThat(channelRepository.findAll().size()).isEqualTo(1);

        AddReadStatusRequest addReadStatusRequest = new AddReadStatusRequest(channel.getId(), user.getId());
        ReadStatus readStatus = readStatusService.addReadStatus(addReadStatusRequest);

        ReadStatus finded = readStatusService.getReadStatus(readStatus.getId());
        Assertions.assertThat(finded).isNotNull();
        Assertions.assertThat(finded.getUserId()).isEqualTo(readStatus.getUserId());
        Assertions.assertThat(finded.getChannelId()).isEqualTo(readStatus.getChannelId());
    }

    @Test
    public void getAllReadStatusByUserIdTest() {
        AddUserRequest addUserRequest1 = new AddUserRequest("testName", "testMail", "testPassword", "testPhone", null);
        AddUserRequest addUserRequest2 = new AddUserRequest("testName1", "testMail1", "testPassword", "testPhone", null);
        User user1 = userService.addUser(addUserRequest1);
        User user2 = userService.addUser(addUserRequest2);

        AddPublicChannelRequest addPublicChannelRequest1 = new AddPublicChannelRequest("testChannelName", "testChannelDesc", user1.getId());
        Channel publicChannel = channelService.addPublicChannel(addPublicChannelRequest1);
        AddPrivateChannelRequest addPrivateChannelRequest = new AddPrivateChannelRequest(user2.getId());
        Channel privateChannel = channelService.addPrivateChannel(addPrivateChannelRequest);

        // user1이 privateChannel에 가입한 상황
        AddReadStatusRequest addReadStatusRequest1 = new AddReadStatusRequest(privateChannel.getId(), user1.getId());
        readStatusService.addReadStatus(addReadStatusRequest1);

        int size = readStatusService.getAllReadStatusByUserId(user1.getId()).size(); // user1은 public, private 둘 다 참가

        Assertions.assertThat(userRepository.findAll().size()).isEqualTo(2);
        Assertions.assertThat(channelRepository.findAll().size()).isEqualTo(2);
        Assertions.assertThat(size).isEqualTo(2);
    }

    @Test
    public void updateReadStatusTest() {
        AddUserRequest addUserRequest1 = new AddUserRequest("testName", "testMail", "testPassword", "testPhone", null);
        User user1 = userService.addUser(addUserRequest1);
        AddPublicChannelRequest addPublicChannelRequest1 = new AddPublicChannelRequest("testChannelName", "testChannelDesc", user1.getId());
        Channel channel = channelService.addPublicChannel(addPublicChannelRequest1);
        AddReadStatusRequest addReadStatusRequest1 = new AddReadStatusRequest(channel.getId(), user1.getId());
        ReadStatus readStatus = readStatusService.addReadStatus(addReadStatusRequest1);
        ReadStatus updatedReadStatus = readStatusService.updateReadStatus(readStatus.getId());

        System.out.println("변경 전: " + readStatus.getLastReadTime());
        System.out.println("변경 후: " + updatedReadStatus.getLastReadTime());
        Assertions.assertThat(updatedReadStatus.getLastReadTime()).isAfterOrEqualTo(readStatus.getLastReadTime());
    }

    @Test void deleteReadStatusTest() {
        AddUserRequest addUserRequest1 = new AddUserRequest("testName", "testMail", "testPassword", "testPhone", null);
        User user1 = userService.addUser(addUserRequest1);

        AddPublicChannelRequest addPublicChannelRequest1 = new AddPublicChannelRequest("testChannelName", "testChannelDesc", user1.getId());
        Channel channel = channelService.addPublicChannel(addPublicChannelRequest1);

        List<ReadStatus> allByUserId = readStatusRepository.findAllByUserId(user1.getId());
        Assertions.assertThat(allByUserId.size()).isEqualTo(1);


        readStatusService.deleteReadStatus(allByUserId.getFirst().getId());

        allByUserId = readStatusRepository.findAllByUserId(user1.getId());
        Assertions.assertThat(allByUserId.size()).isEqualTo(0);
    }

}
