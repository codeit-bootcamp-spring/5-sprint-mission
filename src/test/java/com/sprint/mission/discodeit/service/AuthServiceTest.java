package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.AddUserDto;
import com.sprint.mission.discodeit.dto.response.LoginDto;
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
    AddUserDto addUserDto1;
    AddUserDto addUserDto2;
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
        addUserDto1 = new AddUserDto("testName1", "testMail1", "testPassword1", "testPhone1", null);
        bytes = new byte[]{0x01, 0x02, 0x03, 0x04};
    }

    @Test
    public void loginTest(){
        BinaryContent binaryContent = basicBinaryContentService.addBinaryContent(bytes);
        userService.addUser(addUserDto1);
        addUserDto2 = new AddUserDto("testName2", "testMail2", "testPassword2", "testPhone2", binaryContent.getId());
        userService.addUser(addUserDto2);

        LoginDto noBinaryLogin = authService.login("testName1", "testPassword1");
        LoginDto hasBinaryLogin = authService.login("testName2", "testPassword2");

        Assertions.assertThat(noBinaryLogin.email()).isEqualTo(addUserDto1.email());
        Assertions.assertThat(noBinaryLogin.profileImage()).isNull();

        Assertions.assertThat(hasBinaryLogin.email()).isEqualTo(addUserDto2.email());
        Assertions.assertThat(hasBinaryLogin.profileImage()).isEqualTo(binaryContent.getContent());
    }

    @Test
    public void loginFailUserNameTest(){
        userService.addUser(addUserDto1);

        Assertions.assertThatThrownBy(() -> authService.login("ohHateCoding", "testPassword2")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void loginFailPasswordTest(){
        userService.addUser(addUserDto1);

        Assertions.assertThatThrownBy(() -> authService.login("testMail1", "wwww")).isInstanceOf(IllegalArgumentException.class);
    }
}
