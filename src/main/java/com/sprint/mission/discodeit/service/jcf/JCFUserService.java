package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data = new HashMap<>();


    public User create(String name, String password) {
        User user = new User(name, password);
        data.put(user.getId(), user);
        return user;
    }

    //조회
    public User findById(UUID id) {
        return data.get(id);
    }

    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    // 수정
    public User update(UUID id, String name) {
        User user = data.get(id);
        if (user != null) {
            user.updateName(name);
        }
        return user;
    }

    // 삭제
    public boolean delete(UUID id) {
        if(data.containsKey(id)){
            data.remove(id);
            System.out.println("삭제완료!");
            return true;
        }
        else{
            System.out.println("삭제실패: 사용자를 찾을 수 없습니다");
            return false;
        }
    }
}
