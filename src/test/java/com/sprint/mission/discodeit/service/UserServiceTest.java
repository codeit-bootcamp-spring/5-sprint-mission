package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.binarycontent.BinaryContentRepository;
import com.sprint.mission.discodeit.domain.user.UserRepository;
import com.sprint.mission.discodeit.domain.user.UserService;
import com.sprint.mission.discodeit.domain.user.dto.UserCreateRequest;
import com.sprint.mission.discodeit.domain.user.dto.UserDto;
import com.sprint.mission.discodeit.domain.user.exception.UserAlreadyExistsException;
import com.sprint.mission.discodeit.domain.user.mapper.UserMapper;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공 테스트") // 테스트에 대한 설명 추가
    void createUser_success() {

        UserCreateRequest request = new UserCreateRequest("soyeon", "soyeon@email.com", "1234");
        User createdUser = new User("soyeon", "soyeon@email.com", "1234", null);
        UserDto userDto = UserDto.builder().username("soyeon").email("soyeon@email.com").build();

        BDDMockito.given(userRepository.save(any(User.class))).willReturn(createdUser);
        BDDMockito.given(userMapper.toDto(any(User.class))).willReturn(userDto);

        UserDto resultDto = userService.create(request, Optional.empty());


        // then (검증): 어떤 결과가 나와야 한다
        // 1. 반환된 결과(DTO)의 값이 예상과 일치하는지 검증
        assertThat(resultDto.username()).isEqualTo("soyeon");
        assertThat(resultDto.email()).isEqualTo("soyeon@email.com");

        // 2. Mock 객체(userRepository)의 save 메서드가 정확히 1번 호출되었는지 검증
        BDDMockito.then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 테스트")
    void createUser_failure() {

        UserCreateRequest request = new UserCreateRequest("soyeon", "soyeon@email.com", "1234");
        String existingEmail = request.email();

        BDDMockito.given(userRepository.existsByEmail(existingEmail)).willReturn(true);

        // when & then (실행 및 검증): 예외 발생을 확인
        // userService.create()를 실행했을 때, UserAlreadyExistsException이 발생하는지 검증
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.create(request, Optional.empty());
        });

        // 추가 검증: 발생한 예외의 메시지가 기대와 일치하는지 확인
        assertThat(exception.getMessage()).isEqualTo("이미 존재하는 사용자입니다.");

        BDDMockito.then(userRepository).should().existsByEmail(any());
    }

}
