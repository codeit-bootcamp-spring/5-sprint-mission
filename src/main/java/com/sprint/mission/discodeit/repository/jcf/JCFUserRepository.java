package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    // 사용자 데이터를 저장할 HashMap. UUID를 키로, User 객체를 값으로 사용
    private final Map<UUID, User> users = new HashMap<>();


    @Override
    public User save(User user) {
       users.put(user.getId(), user); // Map에 사용자 저장
        System.out.println("User saved to JCF cache: " + user.getId());
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(UUID id) {

    }
}
