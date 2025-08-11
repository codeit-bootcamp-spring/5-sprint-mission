package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Primary // 같은 타입 서비스가 여러 개면 우선 적용
@RequiredArgsConstructor // final 필드 기반 생성자 자동 생성 (this 생략 가능)

public class FileMessageService implements MessageService {


    private final MessageRepository repository;
    private final UserService userService; //userService.findById쓰기 위해 주입

    @Override
    public void create(Message message) {
        //보낸 유저가 존재하는지 확인
        User sender = userService.findById(message.getSender());
        if (sender == null) {
            throw new IllegalArgumentException("보낸 유저가 존재하지 않습니다.");
        }
        repository.save(message);
    }

    @Override
    public Message findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 메세지 ID가 null입니다.");
        }
        Message original = repository.findById(id);
        if (original == null) {
            throw new IllegalArgumentException("존재하지 않는 메세지입니다.");
        }
        return new Message(original); // 복사본 리턴
    }

    @Override
    public List<Message> findAll() {
        return repository.findAll();
    }

    @Override
    public void update(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("수정할 메세지가 null입니다.");
        }
        if (message.getId() == null) {
            throw new IllegalArgumentException("수정할 메세지 ID가 null입니다.");
        }
        Message original = repository.findById(message.getId());
        if (original == null) {
            throw new IllegalArgumentException("해당 ID의 메세지가 존재하지 않습니다.");
        }
        repository.update(message);
    }

    @Override
    public void delete(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("삭제할 메세지가 null입니다.");
        }
        if (message.getId() == null) {
            throw new IllegalArgumentException("삭제할 메세지 ID가 null입니다.");
        }
        if (repository.findById(message.getId()) == null) {
            throw new IllegalArgumentException("삭제할 메세지가 존재하지 않습니다.");
        }
        repository.delete(message.getId());
    }
}
