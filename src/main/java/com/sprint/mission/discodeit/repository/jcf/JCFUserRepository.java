package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    Map<UUID, User> data = new HashMap<>();

    public JCFUserRepository() {}

    @Override
    public Optional<User> save(User user) {
        if(user == null){
            throw new IllegalArgumentException("user 파라미터가 null 입니다.");
        }

        data.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> findById(UUID userId) {
        if(data.containsKey(userId)){
            return Optional.of(data.get(userId));
        }
        throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(User user) {
        UUID id = user.getId();
        data.remove(id);
    }

    @Override
    public void deleteAll() {
        data.clear();
    }
}
