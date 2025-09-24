package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.domain.user.UserRepository;
import com.sprint.mission.discodeit.domain.user.UserService;
import com.sprint.mission.discodeit.domain.user.dto.UserCreateRequest;
import com.sprint.mission.discodeit.domain.user.dto.UserDto;
import com.sprint.mission.discodeit.domain.user.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.domain.user.exception.UserAlreadyExistsException;
import com.sprint.mission.discodeit.entity.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest             // 전체 스프링 컨테이너를 로드
@ActiveProfiles("test")     // test 프로파일 사용
@Transactional              // 테스트 후 데이터 롤백
public class UserIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("새로운 사용자를 성공적으로 생성한다")
    void createUser_Success() {
        // given
        UserCreateRequest request = new UserCreateRequest("testUser", "test@example.com", "password123");

        // when
        UserDto createdUserDto = userService.create(request, Optional.empty());

        // then
        assertNotNull(createdUserDto.id());
        assertEquals(request.username(), createdUserDto.username());
        assertEquals(request.email(), createdUserDto.email());

        // DB 확인
        Optional<User> foundUserOpt = userRepository.findById(createdUserDto.id());
        assertTrue(foundUserOpt.isPresent());
        assertEquals(request.username(), foundUserOpt.get().getUsername());
    }

    @Test
    @DisplayName("이미 존재하는 username으로 사용자를 생성하면 예외가 발생한다")
    void createUser_Fail_DuplicateUsername() {
        // given
        userRepository.save(new User("existingUser", "unique@example.com", "password", null));
        UserCreateRequest request = new UserCreateRequest("existingUser", "new@example.com", "password123");

        // when & then
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.create(request, Optional.empty())
        );

        assertThat(exception.getMessage()).contains("이미 존재하는 사용자입니다.");
    }

    @Test
    @DisplayName("이미 존재하는 email로 사용자를 생성하면 예외가 발생한다")
    void createUser_Fail_DuplicateEmail() {
        // given
        userRepository.save(new User("uniqueUser", "existing@example.com", "password", null));
        UserCreateRequest request = new UserCreateRequest("newUser", "existing@example.com", "password123");

        // when & then
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.create(request, Optional.empty())
        );

        assertThat(exception.getMessage()).contains("이미 존재하는 사용자입니다.");
    }

    @Test
    @DisplayName("사용자 ID로 사용자를 성공적으로 조회한다")
    void findUser_Success() {
        // given
        UserCreateRequest request = new UserCreateRequest("findUser", "find@example.com", "password123");
        UserDto savedUser = userService.create(request, Optional.empty());

        // when
        UserDto foundUser = userService.find(savedUser.id());

        // then
        assertNotNull(foundUser);
        assertEquals(savedUser.id(), foundUser.id());
        assertEquals(savedUser.username(), foundUser.username());
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 조회하면 예외가 발생한다")
    void findUser_Fail_NotFound() {
        // given
        UUID nonExistentId = UUID.randomUUID();

        // when & then
        assertThrows(
                NoSuchElementException.class,
                () -> userService.find(nonExistentId)
        );
    }

    @Test
    @DisplayName("사용자 정보를 성공적으로 수정한다")
    void updateUser_Success() {
        // given
        UserCreateRequest createRequest = new UserCreateRequest("beforeUser", "before@example.com", "password123");
        UserDto savedUser = userService.create(createRequest, Optional.empty());

        UserUpdateRequest updateRequest = new UserUpdateRequest("afterUser", "after@example.com", "newPassword");

        // when
        UserDto updatedUser = userService.update(savedUser.id(), updateRequest, Optional.empty());

        // then
        assertEquals(savedUser.id(), updatedUser.id());
        assertEquals("afterUser", updatedUser.username());
        assertEquals("after@example.com", updatedUser.email());

        // DB에서도 확인
        User userInDb = userRepository.findById(savedUser.id()).get();
        assertEquals("afterUser", userInDb.getUsername());
    }

    @Test
    @DisplayName("사용자를 성공적으로 삭제한다")
    void deleteUser_Success() {
        // given
        UserCreateRequest createRequest = new UserCreateRequest("deleteUser", "delete@example.com", "password123");
        UserDto savedUser = userService.create(createRequest, Optional.empty());
        UUID userId = savedUser.id();

        // when
        userRepository.deleteById(userId);

        // then
        boolean exists = userRepository.existsById(userId);
        assertFalse(exists);
    }

}
