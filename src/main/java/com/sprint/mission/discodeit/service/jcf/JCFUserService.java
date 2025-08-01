package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/*실제 구현체*/
public class JCFUserService implements UserService {

    //필드 선언
    private final Map<UUID, User> data;


    //생성자에서 초기화
    //data를 빈 HashMap으로 초기화 -> 메세지 담을 공간 만들어둠
    public JCFUserService() {
        this.data = new HashMap<>();
    }


    //오버라이드
    //부모 클래스나 인터페이스에서 정의된 메서드를 자식 클래스에서 재정의
    @Override
    public void create(User user) {
        data.put(user.getId(),user); // 유저 객체 받아 map에 uuid-user 구조로 저장

    }

    @Override
    public User findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(data.values()); //map에 저장된 키값만 꺼내서 불변 리스트로 return해줌
    }

    @Override
    public void update(User user) {
        if (user == null) {
            throw new IllegalArgumentException("USER 값이 NULL입니다.");
        }
        data.put(user.getId(),user); //같은 uuid면 message 값 덮어씀

    }

    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID 값이 NUll입니다.");
        }
        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("해당 ID를 가진 유저가 존재하지 않습니다.");
        }
        data.remove(id);

    }
}
