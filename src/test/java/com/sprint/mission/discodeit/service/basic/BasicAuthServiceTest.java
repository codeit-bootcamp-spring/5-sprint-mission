package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.AddUserDto;
import com.sprint.mission.discodeit.dto.response.LoginDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BasicAuthServiceTest {
    AddUserDto addUserDto1;
    AddUserDto addUserDto2;
    byte[] bytes;

    @Autowired
    private BasicAuthService basicAuthService;
    @Autowired
    private BasicUserService basicUserService;
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
        basicUserService.addUser(addUserDto1);
        addUserDto2 = new AddUserDto("testName2", "testMail2", "testPassword2", "testPhone2", binaryContent.getId());
        basicUserService.addUser(addUserDto2);

        LoginDto noBinaryLogin = basicAuthService.login("testName1", "testPassword1");
        LoginDto hasBinaryLogin = basicAuthService.login("testName2", "testPassword2");

        Assertions.assertThat(noBinaryLogin.email()).isEqualTo(addUserDto1.email());
        Assertions.assertThat(noBinaryLogin.profileImage()).isNull();

        Assertions.assertThat(hasBinaryLogin.email()).isEqualTo(addUserDto2.email());
        Assertions.assertThat(hasBinaryLogin.profileImage()).isEqualTo(binaryContent.getContent());
    }

    @Test
    public void loginFailUserNameTest(){
        basicUserService.addUser(addUserDto1);

        Assertions.assertThatThrownBy(() -> basicAuthService.login("ohHateCoding", "testPassword2")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void loginFailPasswordTest(){
        basicUserService.addUser(addUserDto1);

        Assertions.assertThatThrownBy(() -> basicAuthService.login("testMail1", "wwww")).isInstanceOf(IllegalArgumentException.class);
    }
}
