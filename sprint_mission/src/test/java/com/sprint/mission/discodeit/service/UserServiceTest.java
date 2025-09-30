package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

//mock를 통한 테스트 수행 코드
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BasicUserService userService;

    //테스트에 활용될 객체나 변수
    private UUID userId;
    private String username;
    private String email;
    private String password;

    private User user;
    private UserDto createUserDto;
    private UserDto updateUserDto;

    @BeforeEach
    void setUp(){
        userId = UUID.randomUUID();

        user = new User(
                "test01",
                "test01@email.com",
                "1234",
                null);

        createUserDto = new UserDto(
                userId,
                username,
                email,
                null,
                true);
        updateUserDto = new UserDto(
                userId,
                "홍길동",
                "test2@test.com",
                null,
                true);
    }

    @Test
    @DisplayName("사용자 생성 테스트 (프로필이미지없음)")
    void create_user_without_profile(){
        //given
        username = "test01";
        email = "test01@email.com";
        password = "1234";

        UserCreateRequest req = new UserCreateRequest(
                "testuser", "test@test.com", "1234");

//        if (userRepository.existsByEmail(email)) {
        given(userRepository.existsByEmail(req.email())).willReturn(false);
//        if (userRepository.existsByUsername(username)) {
        given(userRepository.existsByUsername(req.username())).willReturn(false);

//        User user = new User(username, email, password, nullableProfile);
        User user = new User(req.username(),req.email(),req.password(),null);

//        userRepository.save(user);
        given(userRepository.save(any())).willReturn(user);

//        return userMapper.toDto(user);
        given(userMapper.toDto(any())).willReturn(createUserDto);


        //when
        UserDto result = userService.create(req, Optional.empty());

        //then
        assertThat(result).isEqualTo(createUserDto);
        verify(userRepository, times(1)).save(any());
        verify(userMapper, times(1)).toDto(any());
    }

    @Test
    void update_user_without_profile(){
        //given
        username = "홍길동";
        email = "test2@test.com";
        password = "1234";

        UserUpdateRequest req = new UserUpdateRequest(
                "홍길동", "test2@test.com", "1234");

//        User user = userRepository.findById(userId)
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

//        if (userRepository.existsByEmail(newEmail)) {
        given(userRepository.existsByEmail(req.newEmail())).willReturn(false);
//        if (userRepository.existsByUsername(newUsername)) {
        given(userRepository.existsByUsername(req.newUsername())).willReturn(false);
//        return userMapper.toDto(user);
        given(userMapper.toDto(any())).willReturn(updateUserDto);


        //when
//        user.update(newUsername, newEmail, newPassword, nullableProfile);
        UserDto result = userService.update(userId,req,Optional.empty());

        //then
        assertThat(result).isEqualTo(updateUserDto);
        verify(userRepository, times(1)).findById(any());
        verify(userMapper, times(1)).toDto(any());

    }

    @Test
    void delete(){

        //given
//        if (userRepository.existsById(userId)) {
        given(userRepository.existsById(userId)).willReturn(false);

        //when
        userService.delete(userId);

        //then
        verify(userRepository, times(1)).deleteById(userId);

    }



}
