package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

/*실제 구현체*/
public class JCFMessageService implements MessageService {

    //의존성 주입
    private final MessageRepository repository = new JCFMessageRepository();
    private final UserService userService; //userService.findById쓰기 위해 주입

    public JCFMessageService(UserService userService) {
        this.userService = userService;
    }

    //오버라이드
    //부모 클래스나 인터페이스의 메서드를 자식 클래스에서 재정의
    @Override
    public void create(Message message) {
        //유효하지 않은 유저가 메시지를 보내면 안됨
        User sender = userService.findById(message.getSender()); //sender의 UUID 찾음
        if (sender == null) {
            throw new IllegalArgumentException("보낸 유저가 존재하지 않습니다.");
        }
        repository.save(message); //저장은 repository로 위임
    }

    @Override
    public Message findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Message> findAll() {
        return repository.findAll();
    }

    @Override
    public void update(Message message) {
        repository.update(message);
    }

    @Override
    public void delete(Message message) {
        repository.delete(message.getId());
    }
}
