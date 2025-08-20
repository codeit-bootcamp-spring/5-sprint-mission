package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Primary
@Repository
public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> userMap = new HashMap<>();

    @Override
    public void save(User user) {
        userMap.put(user.getId(), user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userMap.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        return userMap.values().stream()
                .filter(user -> user.getUserName().equals(userName))
                .findFirst();
    }

    @Override
    public List<User> findByNickName(String nickname) {
        return userMap.values().stream()
                .filter(user -> user.getNickname().contains(nickname))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAll() {
        return userMap.values().stream().toList();
    }

    @Override
    public boolean delete(UUID id) {
        return userMap.remove(id) != null;
    }
}
