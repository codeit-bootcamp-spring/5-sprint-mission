package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {

    //저장소
    //data를 빈 HashMap으로 초기화 -> 메세지 담을 공간 만들어둠
    private final Map<UUID, User> data = new HashMap<>();

    @Override
    public void save(User user) {
        data.put(user.getId(), user);
    }

    @Override
    public User findById(UUID id) {
        User user = data.get(id);
        if (user == null) {
            throw new IllegalArgumentException("해당 ID를 가진 유저가 존재하지 않습니다");
        }
        return new User(user); // 복사본 리턴, 유저 객체 받아 map에 uuid-user 구조로 저장

    }

    @Override
    public List<User> findAll() {
        return List.copyOf(data.values()); //map에 저장된 키값만 꺼내서 불변 리스트로 return해줌
    }

    @Override
    public void update(User user) {
        if (user == null) {
            throw new IllegalArgumentException("유저 값이 NULL값입니다.");
        }
        data.put(user.getId(), user);
    }

    @Override
    public void delete(UUID id) {
        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("해당 ID를 가진 유저가 존재하지 않습니다.");
        }
        data.remove(id);
    }
}
