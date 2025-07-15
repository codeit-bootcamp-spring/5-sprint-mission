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
    //UUID : 키, Channel : 값 의 구조
    private final Map<UUID, Message> data;


    //생성자 주입
    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    //오버라이드 - 부모 클래스나 인터페이스의 메서드를 자식 클래스에서 재정의
    @Override
    public void create(Message message) {

    }

    @Override
    public Message findById(UUID id) {
        return null;
    }

    @Override
    public List<Message> findAll() {
        return List.of();
    }

    @Override
    public void update(Message message) {

    }

    @Override
    public void delete(Message message) {

    }
}
