package com.sprint.mission.discodeit.respository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    List<User> findAll();
    User findById(UUID id);
    List<User> findByName(String name);

    /**
     * 이메일로 사용자 찾기
     * @param email
     * @return 해당 이메일을 가진 사용자. 존재하지 않으면 Optional.empty()
     **/
    Optional<User> findByEmail(String email);
    User update(UUID id, String name);
    boolean delete(UUID id);
}
