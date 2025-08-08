package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.AddUserDto;
import com.sprint.mission.discodeit.dto.response.GetUserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserStatusServiceTest {

    @Autowired private UserStatusService userStatusService;
    @Autowired private UserService userService;
    @Autowired private UserStatusRepository userStatusRepository;

    @BeforeEach
    public void setUp(){
        userStatusService.deleteAllUserStatus();
        userService.deleteAllUser();
    }

    @Test
    public void addUserStatusTest(){
        AddUserDto addUserDto = new AddUserDto("testName", "testMail", "testPassword", "testPhone", null);
        User user = userService.addUser(addUserDto); // 여기서 UserStatus 객체가 같이 생성됨(User Service 요구사항)

        Assertions.assertThatThrownBy(() -> userStatusService.addUserStatus(user.getId())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getUserStatusByIdTest(){
        AddUserDto addUserDto = new AddUserDto("testName", "testMail", "testPassword", "testPhone", null);
        User user = userService.addUser(addUserDto);

        UserStatus userStatusByUserId = userStatusRepository.findByUserId(user.getId()).orElseThrow(() -> new IllegalArgumentException("UserStatus not found"));
        UserStatus userStatusById = userStatusService.getUserStatusById(userStatusByUserId.getId());

        Assertions.assertThat(userStatusById.getId()).isEqualTo(userStatusByUserId.getId());
    }

    @Test
    public void updateUserStatusTest(){
        AddUserDto addUserDto = new AddUserDto("testName", "testMail", "testPassword", "testPhone", null);
        User user = userService.addUser(addUserDto);

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElseThrow(() -> new IllegalArgumentException("UserStatus not found"));
        System.out.println(userStatus.getLastOnlineTime());

        UserStatus updatedUserStatus = userStatusService.updateUserStatus(userStatus.getId());
        UserStatus userStatusById = userStatusService.getUserStatusById(updatedUserStatus.getId());
        System.out.println(userStatusById.getLastOnlineTime());

        Assertions.assertThat(userStatusById.getLastOnlineTime()).isEqualTo(updatedUserStatus.getLastOnlineTime());
    }

    @Test
    public void deleteUserStatusTest(){
        AddUserDto addUserDto = new AddUserDto("testName", "testMail", "testPassword", "testPhone", null);
        User user = userService.addUser(addUserDto);
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElseThrow(() -> new IllegalArgumentException("UserStatus not found"));

        List<UserStatus> all = userStatusRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(1);

        userStatusService.deleteUserStatus(userStatus.getId());

        all = userStatusRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(0);
    }

}
