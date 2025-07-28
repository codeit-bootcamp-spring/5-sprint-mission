package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
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
    User findById(UUID id);

    /**
     * 이메일로 사용자 정보를 조회합니다.
     * @param email 조회할 사용자 이메일
     * @return 이메일의 사용자, 없으면 null
     */
    User findByEmail(String email);

    /**
     * 사용자 이름으로 사용자 정보를 조회합니다.
     * @param userName 조회할 사용자 이름
     * @return 사용자명에 해당하는 사용자, 없으면 null
     */
    User findByUserName(String userName);

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

    /**
     * 이메일을 기준으로 사용자의 정보를 업데이트합니다.
     *
     * @param email 업데이트 대상 사용자의 이메일
     * @param userName 새 사용자명
     * @param nickname 새 닉네임
     * @param password 새 비밀번호
     * @param phoneNumber 새 전화번호
     * @return 업데이트 성공 여부
     */
    boolean updateByEmail(
            String email,
            String userName,
            String nickname,
            String password,
            String phoneNumber
    );

    /**
     * 사용자명을 기준으로 사용자의 정보를 업데이트합니다.
     *
     * @param userName 업데이트 대상 사용자명
     * @param email 새 이메일
     * @param nickname 새 닉네임
     * @param password 새 비밀번호
     * @param phoneNumber 새 전화번호
     * @return 업데이트 성공 여부
     */
    boolean updateByUserName(
            String userName,
            String email,
            String nickname,
            String password,
            String phoneNumber
    );

    /**
     * 이메일로 사용자를 삭제합니다.
     * @param email 삭제할 사용자 이메일
     * @return 삭제 성공 여부
     */
    boolean deleteByEmail(String email);

    /**
     * 사용자명으로 사용자를 삭제합니다.
     * @param userName 삭제할 사용자명
     * @return 삭제 성공 여부
     */
    boolean deleteByUserName(String userName);
}
