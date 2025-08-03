package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {

    //저장소
    private final Map<UUID, Message> data = new HashMap<>();


    @Override
    public void save(Message message) {
        data.put(message.getId(), message);
    }

    @Override
    public Message findById(UUID id) {
        Message message = data.get(id);
        if (message == null) {
            throw new IllegalArgumentException("해당 ID를 가진 메세지가 없습니다.");
        }
        return new Message(message);
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data.values()); //copyOf: map에 저장된 키값만 꺼내서 외부에서 수정못하게 막아줌
    }

    @Override
    public void update(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("메세지가 NULL입니다.");
        }
        data.put(message.getId(), message); //같은 uuid면 message 값 덮어씀
    }

    @Override
    public void delete(UUID id) {
        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("해당 ID를 가진 메세지가 없습니다.");
        }
        data.remove(id);
    }
}
