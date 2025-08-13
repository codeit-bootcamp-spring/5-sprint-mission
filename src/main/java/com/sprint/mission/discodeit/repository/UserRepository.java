package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    /**
     * 주어진 User 객체를 저장합니다.
     */
    void save(User user);

    /**
     * ID(UUID)로 사용자 정보를 조회합니다.
     * @param id 조회할 사용자 UUID
     * @return UUID의 사용자, 없으면 null
     */
    Optional<User> findById(UUID id);

    /**
     * 이메일로 사용자 정보를 조회합니다.
     * @param email 조회할 사용자 이메일
     * @return 이메일의 사용자, 없으면 null
     */
    Optional<User> findByEmail(String email);

    /**
     * 사용자 이름으로 사용자 정보를 조회합니다.
     * @param userName 조회할 사용자 이름
     * @return 사용자명에 해당하는 사용자, 없으면 null
     */
    Optional<User> findByUserName(String userName);

    /**
     * 닉네임으로 사용자 목록을 조회합니다.
     * 부분 일치 검색이 가능합니다.
     *
     * @param nickname 조회할 사용자 닉네임
     * @return 닉네임이 일치하는 사용자 목록 (부분 일치 가능)
     */
    List<User> findByNickName(String nickname);

    /**
     * 모든 사용자 정보를 조회합니다.
     * @return 사용자 목록
     */
    List<User> findAll();

    boolean delete(UUID id);
}
