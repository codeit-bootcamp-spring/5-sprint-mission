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
        // Map에서 UUID를 사용하여 사용자 조회
        // get(id)는 해당 키가 없으면 null을 반환하므로, Optional.ofNullable()을 사용해서 비어있는 Optional을 반환하도록 함
        System.out.println("Findig user by ID in JCF cache: " + id);
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        System.out.println("Finding user by email in JCF cache: " + email);
        return users.values().stream()
                .filter(user -> user.getEmail() != null && user.getEmail().equals(email))
                .findFirst(); // 필터링된 요소 중 가장 첫번째로 필터링된 요소를 수집하여 반환 -> Email 중복이여도 첫번째 email만 반환
    }

    @Override
    public Optional<User> findByName(String name) {
        System.out.println("Finding user by name in JCF cache: " + name);
        return users.values().stream()
                .filter(user -> user.getName() != null && user.getName().equals(name))
                .findFirst(); // email과 마찬가지로 가장 첫번째 필터링된 요소를 수집하여 반환 -> 중복 x
    }

    @Override
    public List<User> findAll() {
        System.out.println("Retrieving all users from JCF cache. Total: " + users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(UUID id) {
        if(!users.containsKey(id)) {
            throw new NoSuchElementException("User with id " + id + " not found");
        }
        users.remove(id);
        System.out.println("User deleted from JCF cache: "+ id);
    }
}
