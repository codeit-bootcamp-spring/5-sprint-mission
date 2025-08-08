package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.AddPublicChannelDto;
import com.sprint.mission.discodeit.dto.request.AddReadStatusDto;
import com.sprint.mission.discodeit.dto.request.AddUserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import net.bytebuddy.build.ToStringPlugin;
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
        AddUserDto addUserDto = new AddUserDto("testName", "testMail", "testPassword", "testPhone", null);
        User user = userService.addUser(addUserDto);

        Assertions.assertThat(userRepository.findAll().size()).isEqualTo(1);
        AddPublicChannelDto addPublicChannelDto1 = new AddPublicChannelDto("testChannelName", "testChannelDesc", user.getId());
        Channel channel = channelService.addPublicChannel(addPublicChannelDto1);
        Assertions.assertThat(channelRepository.findAll().size()).isEqualTo(1);

        AddReadStatusDto addReadStatusDto = new AddReadStatusDto(channel.getId(), user.getId());
        ReadStatus readStatus = readStatusService.addReadStatus(addReadStatusDto);

        Assertions.assertThat(readStatus).isNotNull();
        Assertions.assertThat(readStatus.getUserId()).isEqualTo(addReadStatusDto.userId());
        Assertions.assertThat(readStatus.getChannelId()).isEqualTo(addReadStatusDto.channelId());
    }

    @Test
    public void getReadStatusTest() {
        AddUserDto addUserDto = new AddUserDto("testName", "testMail", "testPassword", "testPhone", null);
        User user = userService.addUser(addUserDto);

        Assertions.assertThat(userRepository.findAll().size()).isEqualTo(1);
        AddPublicChannelDto addPublicChannelDto1 = new AddPublicChannelDto("testChannelName", "testChannelDesc", user.getId());
        Channel channel = channelService.addPublicChannel(addPublicChannelDto1);
        Assertions.assertThat(channelRepository.findAll().size()).isEqualTo(1);

        AddReadStatusDto addReadStatusDto = new AddReadStatusDto(channel.getId(), user.getId());
        ReadStatus readStatus = readStatusService.addReadStatus(addReadStatusDto);

        ReadStatus finded = readStatusService.getReadStatus(readStatus.getId());
        Assertions.assertThat(finded).isNotNull();
        Assertions.assertThat(finded.getUserId()).isEqualTo(readStatus.getUserId());
        Assertions.assertThat(finded.getChannelId()).isEqualTo(readStatus.getChannelId());
    }

    @Test
    public void getAllReadStatusByUserIdTest() {
        AddUserDto addUserDto1 = new AddUserDto("testName", "testMail", "testPassword", "testPhone", null);
        AddUserDto addUserDto2 = new AddUserDto("testName1", "testMail1", "testPassword", "testPhone", null);
        User user1 = userService.addUser(addUserDto1);
        User user2 = userService.addUser(addUserDto2);

        AddPublicChannelDto addPublicChannelDto1 = new AddPublicChannelDto("testChannelName", "testChannelDesc", user1.getId());
        Channel channel = channelService.addPublicChannel(addPublicChannelDto1);

        AddReadStatusDto addReadStatusDto1 = new AddReadStatusDto(channel.getId(), user1.getId());
        AddReadStatusDto addReadStatusDto2 = new AddReadStatusDto(channel.getId(), user2.getId());
        readStatusService.addReadStatus(addReadStatusDto1);
        readStatusService.addReadStatus(addReadStatusDto1);
        readStatusService.addReadStatus(addReadStatusDto2);

        int size = readStatusService.getAllReadStatusByUserId(user1.getId()).size();
        Assertions.assertThat(userRepository.findAll().size()).isEqualTo(2);
        Assertions.assertThat(channelRepository.findAll().size()).isEqualTo(1);
        Assertions.assertThat(size).isEqualTo(2);
    }

    @Test
    public void updateReadStatusTest() {
        AddUserDto addUserDto1 = new AddUserDto("testName", "testMail", "testPassword", "testPhone", null);
        User user1 = userService.addUser(addUserDto1);
        AddPublicChannelDto addPublicChannelDto1 = new AddPublicChannelDto("testChannelName", "testChannelDesc", user1.getId());
        Channel channel = channelService.addPublicChannel(addPublicChannelDto1);
        AddReadStatusDto addReadStatusDto1 = new AddReadStatusDto(channel.getId(), user1.getId());
        ReadStatus readStatus = readStatusService.addReadStatus(addReadStatusDto1);
        ReadStatus updatedReadStatus = readStatusService.updateReadStatus(readStatus.getId());

        System.out.println("변경 전: " + readStatus.getLastReadTime());
        System.out.println("변경 후: " + updatedReadStatus.getLastReadTime());
        Assertions.assertThat(updatedReadStatus.getLastReadTime()).isAfterOrEqualTo(readStatus.getLastReadTime());
    }

    @Test void deleteReadStatusTest() {
        AddUserDto addUserDto1 = new AddUserDto("testName", "testMail", "testPassword", "testPhone", null);
        User user1 = userService.addUser(addUserDto1);
        AddPublicChannelDto addPublicChannelDto1 = new AddPublicChannelDto("testChannelName", "testChannelDesc", user1.getId());
        Channel channel = channelService.addPublicChannel(addPublicChannelDto1);
        AddReadStatusDto addReadStatusDto1 = new AddReadStatusDto(channel.getId(), user1.getId());
        ReadStatus readStatus = readStatusService.addReadStatus(addReadStatusDto1);


        List<ReadStatus> allByUserId = readStatusRepository.findAllByUserId(user1.getId());
        Assertions.assertThat(allByUserId.size()).isEqualTo(1);

        readStatusService.deleteReadStatus(readStatus.getId());

        allByUserId = readStatusRepository.findAllByUserId(user1.getId());
        Assertions.assertThat(allByUserId.size()).isEqualTo(0);
    }

}
