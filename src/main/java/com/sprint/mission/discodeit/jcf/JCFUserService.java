package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        data = new HashMap<>();
    }

    @Override
    public User register(User user) {
        if (isInvalid(user.getName()) || isInvalid(user.getPassword()))
            throw new IllegalArgumentException("사용자 등록에 실패했습니다.");

        data.put(user.getId(), user);
        System.out.println("사용자 : " + user.getName() + " 등록 성공.");
        return user;
    }

    @Override
    public User findById(UUID id) {
        if (!data.containsKey(id))
            throw new NoSuchElementException("사용자에서 해당 " + id + "를 찾을 수 없습니다.");
        return data.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID id, String newPW) {
        if (isInvalid(newPW))
            throw new IllegalArgumentException("새로운 비밀번호를 입력하세요.");

        User user = findById(id);

        user.setPassword(newPW);
        user.setUpdatedAt(System.currentTimeMillis());
        return user;
    }

    @Override
    public User delete(UUID id) {
        if (!data.containsKey(id))
            throw new NoSuchElementException("사용자에서 해당 " + id + "를 찾을 수 없습니다.");
        else
            return data.remove(id);
    }

    public boolean isInvalid(String value) {
        return value == null || value.isBlank();
    }
}
