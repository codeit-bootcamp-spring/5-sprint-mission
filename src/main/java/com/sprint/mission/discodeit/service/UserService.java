package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    /**
     * 새로운 사용자를 등록합니다.
     *
     * @param email 사용자 이메일 (고유)
     * @param userName 사용자 이름 (고유)
     * @param nickname 사용자 닉네임
     * @param password 사용자 비밀번호
     * @param phoneNumber 사용자 전화번호
     * @return 등록 성공 여부 (이메일 또는 이름 중복 시 false)
     */
    boolean register(
            String email,
            String userName,
            String nickname,
            String password,
            String phoneNumber
    );

    /**
     * 사용자 ID(UUID)로 사용자를 조회합니다.
     *
     * @param id 사용자 ID
     * @return UUID의 사용자, 없으면 null
     */
    User getById(UUID id);

    /**
     * 이메일로 사용자를 조회합니다.
     *
     * @param email 조회할 이메일
     * @return 이메일의 사용자, 없으면 null
     */
    User getByEmail(String email);

    /**
     * 사용자 이름으로 사용자 조회합니다.
     *
     * @param userName 사용자 이름
     * @return 사용자명에 해당하는 사용자, 없으면 null
     */
    User getByUserName(String userName);

    /**
     * 닉네임으로 사용자 리스트를 검색합니다.
     *
     * @param nickname 검색할 닉네임
     * @return 닉네임이 일치하는 사용자 목록 (부분 일치 가능)
     */
    List<User> searchByNickname(String nickname);

    /**
     * 모든 사용자 목록을 조회합니다.
     *
     * @return 전체 사용자 목록
     */
    List<User> getAll();

    /**
     * 이메일로 사용자를 찾아 정보를 수정합니다.
     *
     * @param email 대상 이메일
     * @param userName 새 사용자명
     * @param nickname 새 닉네임
     * @param password 새 비밀번호
     * @param phoneNumber 새 전화번호
     * @return 수정 성공 여부
     */
    boolean updateByEmail(
            String email,
            String userName,
            String nickname,
            String password,
            String phoneNumber
    );

    /**
     * 사용자명으로 사용자를 찾아 정보를 수정합니다.
     *
     * @param userName 대상 사용자명
     * @param email 새 이메일
     * @param nickname 새 닉네임
     * @param password 새 비밀번호
     * @param phoneNumber 새 전화번호
     * @return 수정 성공 여부
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
     *
     * @param email 삭제할 사용자 이메일
     * @return 삭제 성공 여부
     */
    boolean removeByEmail(String email);

    /**
     * 사용자명으로 사용자를 삭제합니다.
     *
     * @param userName 삭제할 사용자명
     * @return 삭제 성공 여부
     */
    boolean removeByUserName(String userName);
}
