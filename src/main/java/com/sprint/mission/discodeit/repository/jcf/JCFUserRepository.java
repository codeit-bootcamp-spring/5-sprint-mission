package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

public class JCFUserRepository extends AbstractJCFRepository<User> implements UserRepository {

    // TODO mission 조건에 맞도록 추후 구현 existsByUsername, existsByEmail
    @Override
    public boolean existsByUsername(String username) {
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }
}
