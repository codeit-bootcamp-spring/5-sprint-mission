package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    User create(UserDto.Create dto);

    User update(UserDto.Update dto);

    /**
     * 아이디로 유저 조회
     *
     * @param id  유저 아이디
     * @return 찾은 User 객체
     */
    Optional<UserDto.View> findById(UUID id);

    /**
     * 아이디로 유저 조회
     *
     * @param email  유저 이메일
     * @return 찾은 User 객체
     */
    Optional<UserDto.View> findByEmail(String email);

    /**
     * 아이디로 유저목록 조회
     * @return 찾은 User 객체 리스트
     */
    List<UserDto.View> findAll();




    /**
     * 아이디로 유저삭제
     */
    boolean delete(UUID id);

}
