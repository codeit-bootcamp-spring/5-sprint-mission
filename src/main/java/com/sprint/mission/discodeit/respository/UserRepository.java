package com.sprint.mission.discodeit.respository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    /**
     * 사용자를 저장
     *
     * @param user 저장할 사용자 객체
     * @return 저장된 사용자 객체
     */
    User save(User user);

    /**
     * 모든 사용자 목록을 조회
     *
     * @return 저장된 모든 사용자 리스트
     */
    List<User> findAll();

    /**
     * ID를 기준으로 사용자를 조회
     *
     * @param id 조회할 사용자의 UUID
     * @return 해당 ID의 사용자. 존재하지 않으면 Optional.empty()
     */
    Optional<User> findById(UUID id);

    /**
     * 이름을 기준으로 사용자 목록을 조회
     *
     * @param name 조회할 사용자 이름
     * @return 해당 이름을 가진 사용자 리스트 (중복 가능성 있음)
     */
    List<User> findByName(String name);

    /**
     * 이메일을 기준으로 사용자를 조회
     *
     * @param email 조회할 사용자 이메일
     * @return 해당 이메일을 가진 사용자. 존재하지 않으면 Optional.empty()
     */
    Optional<User> findByEmail(String email);

    /**
     * 사용자 이름을 수정
     *
     * @param id    수정할 사용자 ID
     * @param name  새로운 이름
     * @return 수정된 사용자 객체
     */
    User update(UUID id, String name);

    /**
     * 사용자를 삭제
     *
     * @param id 삭제할 사용자 ID
     * @return 삭제 성공 시 true, 실패 시 false
     */
    boolean delete(UUID id);
}
