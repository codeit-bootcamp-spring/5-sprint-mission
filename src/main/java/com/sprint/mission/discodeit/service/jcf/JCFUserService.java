package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new  HashMap<>();
    }

    @Override
    public User create(String name, String email, String password) {
        User user = new User(
                name,
                email,
                password
        );
        data.put(user.getId(), user);
        return user;
    }

    // 옵셔널을 사용하면 코드를 간략화 할 수 있다 (중요)
    @Override
    public User findById(UUID id) {
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
    }

    // stream().toList() 메서드를 사용하면 새로운 불변 리스트를 반환한다. (중요)
    // 복사본 생성 -> 불변 리스트로 변환
    @Override
    public List<User> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public User update(UUID id, String name, String email, String password) {
        User user = Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
        user.update(name, email, password);
        return user;
    }

    // data.get(id) == null 검사 추가
    @Override
    public void delete(UUID id) {
        if (!data.containsKey(id) || data.get(id) == null) {
            throw new NoSuchElementException("User with id " + id + " not found");

        }

        data.remove(id);
    }
}