package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data = new HashMap<>();

    // 생성
    public User create(String name, String password) {
        User user = new User(name, password);
        data.put(user.getId(), user);
        return user;
    }

    //조회
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    // 수정
    public User update(UUID id, String name) {
        User user = data.get(id);
        if (user == null) {
            throw new NoSuchElementException("해당 ID의 사용자를 찾을 수 없습니다");
        }
        user.updateName(name);
        return user;
    }

    // 삭제
    public void delete(UUID id) {
        boolean deleted = data.remove(id) != null;
        System.out.println(deleted ? "삭제완료!" : "삭제실패: 사용자를 찾을 수 없습니다");
    }
}
