package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.AddUserRequest;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.LoginResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthServiceTest {
    AddUserRequest addUserRequest1;
    AddUserRequest addUserRequest2;
    byte[] bytes;

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private BinaryContentService basicBinaryContentService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BinaryContentRepository binaryContentRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;


    @BeforeEach
    public void setUp(){
        userRepository.deleteAll();
        binaryContentRepository.deleteAll();
        userStatusRepository.deleteAll();
        addUserRequest1 = new AddUserRequest("testName1", "testMail1", "testPassword1", "testPhone1", null);
        bytes = new byte[]{0x01, 0x02, 0x03, 0x04};
    }

    @Test
    public void loginTest(){
        BinaryContent binaryContent = basicBinaryContentService.addBinaryContent(bytes);
        userService.addUser(addUserRequest1);
        addUserRequest2 = new AddUserRequest("testName2", "testMail2", "testPassword2", "testPhone2", binaryContent.getId());
        userService.addUser(addUserRequest2);

        LoginResponse noBinaryLogin = authService.login(new LoginRequest("testName1", "testPassword1"));
        LoginResponse hasBinaryLogin = authService.login(new LoginRequest("testName2", "testPassword2"));

        Assertions.assertThat(noBinaryLogin.email()).isEqualTo(addUserRequest1.email());
        Assertions.assertThat(noBinaryLogin.profileImage()).isNull();

        Assertions.assertThat(hasBinaryLogin.email()).isEqualTo(addUserRequest2.email());
        Assertions.assertThat(hasBinaryLogin.profileImage()).isEqualTo(binaryContent.getContent());
    }

    @Test
    public void loginFailUserNameTest(){
        userService.addUser(addUserRequest1);

        Assertions.assertThatThrownBy(() -> authService.login(new LoginRequest("ohHateCoding", "testPassword2"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void loginFailPasswordTest(){
        userService.addUser(addUserRequest1);

        Assertions.assertThatThrownBy(() -> authService.login(new LoginRequest("testMail1", "wwww"))).isInstanceOf(IllegalArgumentException.class);
    }
}
