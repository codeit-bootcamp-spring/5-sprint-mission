package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

/*실제 구현체*/
public class JCFUserService implements UserService {


    private final UserRepository repository = new JCFUserRepository();

    //오버라이드
    //부모 클래스나 인터페이스에서 정의된 메서드를 자식 클래스에서 재정의
    @Override
    public void create(User user) {
        repository.save(user);
    }

    @Override
    public User findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public void update(User user) {
        repository.update(user);
    }

    @Override
    public void delete(UUID id) {
        repository.delete(id);
    }
}
