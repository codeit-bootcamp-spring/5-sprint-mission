package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks // 선언 된 @Mock 객체 기반으로 자동으로 주입하는 어노테이션
    private BasicUserService userService;

    private UUID userId;
    private String username;
    private String email;
    private String password;

    private String newusername;
    private String newemail;
    private String newpassword;

    private User user;
    private User newUser;
    private UserDto userDto;
    private UserDto newUserDto;

    @BeforeEach
    void setUp(){
        userId = UUID.randomUUID();
        username = "test01";
        email = "test01@email.com";
        password = "password1234";

        newusername = "test02";
        newemail = "test02@email.com";
        newpassword = "password12345";

        user = User.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();

        userDto = new UserDto(
                userId,
                username,
                password,
                null,
                null
        );

        newUserDto = new UserDto(
                userId,
                newusername,
                newpassword,
                null,
                null
        );
    }


    @Test
    @DisplayName("사용자 생성 테스트(아바타 없음) - 성공")
    void create_user_without_avatar(){
        // given
        UserCreateRequest req = new UserCreateRequest(
                username, password, email);

        // given 모의 설정(Mock 시나리오 설정)
        // User user = userMapper.toUser(newUser);
        given(userMapper.toDto(any())).willReturn(userDto);

        // userRepository.existsByUsername(user.getUsername())
        given(userRepository.existsByUsername(username)).willReturn(false);

        given(userRepository.existsByEmail("password1234")).willReturn(false);

        // userRepository.save(user);
        given(userRepository.save(any())).willReturn(user);

        Optional<BinaryContentCreateRequest> optionalProfile = Optional.empty();
        // when
        UserDto result = userService.create(req, optionalProfile);

        // then
        assertThat(result).isEqualTo(userDto);
        verify(userRepository, times(1)).save(any());
        verify(userMapper, times(1)).toDto(any());
    }

//    @Test
//    @DisplayName("사용자 생성 테스트(아바타 없음) - 실패")
//    void create_user_without_avatar_fail(){
//        // given
//        UserCreateRequest req = new UserCreateRequest(
//                username, password, email);
//
//        // given 모의 설정(Mock 시나리오 설정)
//        // User user = userMapper.toUser(newUser);
//        given(userMapper.toDto(any())).willReturn(userDto);
//
//        // userRepository.existsByUsername(user.getUsername())
//        given(userRepository.existsByUsername(username)).willReturn(true);
//
//        given(userRepository.existsByEmail("password1234")).willReturn(false);
//
//        // userRepository.save(user);
//        given(userRepository.save(any())).willReturn(user);
//
//        Optional<BinaryContentCreateRequest> optionalProfile = Optional.empty();
//        // when
//        UserDto result = userService.create(req, optionalProfile);
//
//        // then
//        assertThat(result).isEqualTo(userDto);
//        verify(userRepository, times(1)).save(any());
//        verify(userMapper, times(1)).toDto(any());
//    }


    @Test
    @DisplayName("사용자 수정 테스트(아바타 없음) - 성공")
    void update_user_without_avatar(){
        // given
        UserUpdateRequest req = new UserUpdateRequest(
                newusername, newpassword, newemail);

        // given 모의 설정(Mock 시나리오 설정)
        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));

        given(userMapper.toDto(any())).willReturn(newUserDto);
        // userRepository.existsByUsername(user.getUsername())
        given(userRepository.existsByUsername(newusername)).willReturn(false);

        given(userRepository.existsByEmail("password12345")).willReturn(false);

        Optional<BinaryContentCreateRequest> optionalProfile = Optional.empty();
        // when
        UserDto result = userService.update(userId, req, optionalProfile);

        // then
        assertThat(result).isEqualTo(newUserDto);
    }


//    @Test
//    @DisplayName("사용자 수정 테스트(아바타 없음) - 실패")
//    void update_user_without_avatar_fail(){
//        // given
//        UserUpdateRequest req = new UserUpdateRequest(
//                newusername, newpassword, newemail);
//
//        // given 모의 설정(Mock 시나리오 설정)
//        given(userRepository.findById(any())).willReturn(Optional.ofNullable(user));
//
//        given(userMapper.toDto(any())).willReturn(newUserDto);
//        // userRepository.existsByUsername(user.getUsername())
//        given(userRepository.existsByUsername(newusername)).willReturn(true);
//
//        given(userRepository.existsByEmail("password12345")).willReturn(true);
//
//        Optional<BinaryContentCreateRequest> optionalProfile = Optional.empty();
//        // when
//        UserDto result = userService.update(userId, req, optionalProfile);
//
//        // then
//        assertThat(result).isEqualTo(newUserDto);
//    }

    @Test
    @DisplayName("사용자 삭제 테스트 - 성공")
    void delete_user(){

        // given 모의 설정(Mock 시나리오 설정)
        given(userRepository.existsById(userId)).willReturn(false);

        // when
        userService.delete(userId);

        // then
        verify(userRepository, times(1)).existsById(userId);
    }

//    @Test
//    @DisplayName("사용자 삭제 테스트 - 실패")
//    void delete_user_fail(){
//
//        // given 모의 설정(Mock 시나리오 설정)
//        given(userRepository.existsById(userId)).willReturn(true);
//
//        // when
//        userService.delete(userId);
//
//        // then
//        verify(userRepository, times(1)).existsById(userId);
//    }


}
