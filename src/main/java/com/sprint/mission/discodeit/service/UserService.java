package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {


    User registerUser(String name, String email, String password); // 새로운 사용자 등록 -> 이메일 중복 검사 등의 비즈니스 로직을 포함
    Optional<User> loginUser (String email, String password); // 사용자 로그인 기능 -> 로그인 성공 시 User 객체를 포함하는 Optional / 실패 시 Optional.empty()
    Optional<User> findById (UUID id); // 아이디로 사용자 검색
    Optional<User> findByEmail(String email); // 이메일로 사용자 검색
    Optional<User> findByName(String name);
    boolean deleteUser(UUID id);
}
