package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    /**
     * 새로운 유저 생성
     *
     * @param name  채널 이름
     * @param password 채널 주제
     * @return 생성된 User 객체
     */
    User create(String name, String password);

    /**
     * 아이디로 유저 조회
     *
     * @param id  유저 아이디
     * @return 찾은 User 객체
     */
    Optional<User> findById(UUID id);

    /**
     * 아이디로 유저목록 조회
     * @return 찾은 User 객체 리스트
     */
    List<User> findAll();

    /**
     * 유저 이름 변경
     * @param id 유저 아이디
     * @param name 유저 이름
     * @return 업데이트된 User 객체
     */
    User update(UUID id, String name);

    /**
     * 아이디로 유저삭제
     */
    boolean delete(UUID id);

}
