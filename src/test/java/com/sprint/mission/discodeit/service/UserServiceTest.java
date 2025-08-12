package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.AddUserRequest;
import com.sprint.mission.discodeit.dto.response.GetUserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService basicUserService;
    @Autowired
    private BinaryContentService binaryContentService;
    @Autowired
    private UserStatusService userStatusService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BinaryContentRepository binaryContentRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;

    AddUserRequest addUserDao1;
    AddUserRequest addUserDao2;
    @Autowired
    private UserService userService;


    @BeforeEach
    public void setUp(){
        userRepository.deleteAll();
        binaryContentRepository.deleteAll();
        userStatusRepository.deleteAll();
        addUserDao1 = new AddUserRequest("testName1", "testMail1", "testPassword1", "testPhone1", null);
        addUserDao2 = new AddUserRequest("testName2", "testMail2", "testPassword2", "testPhone2", null);
    }

    @Test
    public void testAddUser() {
        User user = basicUserService.addUser(addUserDao1);
        GetUserResponse userById = basicUserService.getUserById(user.getId());

        Assertions.assertThat(user.getId()).isEqualTo(userById.userId());
        Assertions.assertThat(user.getUserName()).isEqualTo(userById.username());
        Assertions.assertThat(user.getEmail()).isEqualTo(userById.email());
        Assertions.assertThat(user.getPhoneNumber()).isEqualTo(userById.phoneNumber());
        Assertions.assertThat(user.getProfileId()).isEqualTo(userById.profileId());

        int size = userRepository.findAll().size();
        Assertions.assertThat(size).isEqualTo(1);
    }

    @Test
    public void testAddUserDuplicatedUserName() {
        AddUserRequest duplicateUserName = new AddUserRequest("testName1", "testMail999", "testPassword999", "testPhone999", null);
        basicUserService.addUser(addUserDao1);
        Assertions.assertThatThrownBy(() -> basicUserService.addUser(duplicateUserName)).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    public void testAddUserDuplicatedEmail() {
        AddUserRequest duplicateUserName = new AddUserRequest("testName999", "testMail1", "testPassword999", "testPhone999", null);
        basicUserService.addUser(addUserDao1);
        Assertions.assertThatThrownBy(() -> basicUserService.addUser(duplicateUserName)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getUserByIdTest(){
        User user = basicUserService.addUser(addUserDao1);
        GetUserResponse userById = basicUserService.getUserById(user.getId());

        Assertions.assertThat(userById.userId()).isEqualTo(user.getId());
        Assertions.assertThat(userById.username()).isEqualTo(user.getUserName());
        Assertions.assertThat(userById.email()).isEqualTo(user.getEmail());
        Assertions.assertThat(userById.phoneNumber()).isEqualTo(user.getPhoneNumber());
        Assertions.assertThat(userById.profileId()).isEqualTo(user.getProfileId());
        Assertions.assertThat(userById.isOnline()).isEqualTo(true);
    }

    @Test
    public void getAllUserTest(){
        basicUserService.addUser(addUserDao1);
        basicUserService.addUser(addUserDao2);
        List<GetUserResponse> allUser = basicUserService.getAllUser();
        Assertions.assertThat(allUser.size()).isEqualTo(2);
    }

    @Test
    public void updateUserTest(){
        User target = basicUserService.addUser(addUserDao1);
        AddUserRequest updateInfo = new AddUserRequest("testName3", "testMail3", "testPassword3", "testPhone3", UUID.randomUUID());

        User result = basicUserService.updateUser(target.getId(), updateInfo);

        Assertions.assertThat(result.getUserName()).isEqualTo(updateInfo.userName());
        Assertions.assertThat(result.getEmail()).isEqualTo(updateInfo.email());
        Assertions.assertThat(result.getPhoneNumber()).isEqualTo(updateInfo.phoneNumber());
        Assertions.assertThat(result.getProfileId()).isEqualTo(updateInfo.profileId());
        Assertions.assertThat(result.getId()).isEqualTo(target.getId());
    }

    @Test
    public void deleteUserTest(){
        // User를 삭제하면 UserStatus, BinaryContent도 삭제되어야 한다.
        byte[] bytes = {0x01, 0x02, 0x03, 0x04};
        BinaryContent addedBinaryContent1 = binaryContentService.addBinaryContent(bytes);
        BinaryContent addedBinaryContent2 = binaryContentService.addBinaryContent(bytes);
        BinaryContent addedBinaryContent3 = binaryContentService.addBinaryContent(bytes);

        addUserDao1 = new AddUserRequest("testName1", "testMail1", "testPassword1", "testPhone1", addedBinaryContent1.getId());
        addUserDao2 = new AddUserRequest("testName2", "testMail2", "testPassword2", "testPhone2", addedBinaryContent2.getId());
        AddUserRequest addUserDao3 = new AddUserRequest("testName3", "testMail3", "testPassword3", "testPhone3", addedBinaryContent3.getId());

        User addedUser1 = basicUserService.addUser(addUserDao1);
        basicUserService.addUser(addUserDao2);
        basicUserService.addUser(addUserDao3);

        List<GetUserResponse> allUser = basicUserService.getAllUser();
        List<BinaryContent> allBinaryContent = binaryContentService.getAllBinaryContent();
        List<UserStatus> allUserStatus = userStatusService.getAllUserStatus();

        Assertions.assertThat(allUser.size()).isEqualTo(3);
        Assertions.assertThat(allBinaryContent.size()).isEqualTo(3);
        Assertions.assertThat(allUserStatus.size()).isEqualTo(3);

        basicUserService.deleteUser(addedUser1.getId());

        allUser = basicUserService.getAllUser();
        allBinaryContent = binaryContentService.getAllBinaryContent();
        allUserStatus = userStatusService.getAllUserStatus();

        Assertions.assertThat(allUser.size()).isEqualTo(2);
        Assertions.assertThat(allBinaryContent.size()).isEqualTo(2);
        Assertions.assertThat(allUserStatus.size()).isEqualTo(2);

        userService.deleteAllUser();

        allUser = basicUserService.getAllUser();
        allBinaryContent = binaryContentService.getAllBinaryContent();
        allUserStatus = userStatusService.getAllUserStatus();

        Assertions.assertThat(allUser.size()).isEqualTo(0);
        Assertions.assertThat(allBinaryContent.size()).isEqualTo(0);
        Assertions.assertThat(allUserStatus.size()).isEqualTo(0);
    }
}
