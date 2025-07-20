package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/*실제 구현체*/
public class JCFMessageService implements MessageService {

    //필드 선언
    //키:값 구조
    private final Map<UUID, Message> data;


    //생성자 주입
    //data를 빈 HashMap으로 초기화 -> 메세지 담을 공간 만들어둠
    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    //오버라이드
    //부모 클래스나 인터페이스의 메서드를 자식 클래스에서 재정의
    @Override
    public void create(Message message) {
        data.put(message.getId(), message); //메세지 객체 받아 map에 uuid-메세지 구조로 저장

    }

    @Override
    public Message findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data.values()); //map에 저장된 키값만 꺼내서 불변 리스트로 return 해줌
    }

    @Override
    public void update(Message message) {
        data.put(message.getId(), message); //같은 uuid면 message 값 덮어씀
    }

    @Override
    public void delete(Message message) {
        data.remove(message.getId());
    }
}
