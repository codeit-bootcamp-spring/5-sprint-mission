package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/*실제 구현체*/
public class JCFMessageService implements MessageService {

    //필드 선언
    //키:값 구조
    private final Map<UUID, Message> data;
    private final UserService userService; //userService.findById쓰기 위해 주입

    //생성자 주입
    //data를 빈 HashMap으로 초기화 -> 메세지 담을 공간 만들어둠
    public JCFMessageService(UserService userService) {
        this.data = new HashMap<>();
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
        data.put(message.getId(), message); //메세지 객체 받아 map에 uuid-메세지 구조로 저장
    }

    @Override
    public Message findById(UUID id) {
        Message message = data.get(id);
        if (message == null) {
            throw new IllegalArgumentException("아이디가 NULL값입니다.");
        }
        return new Message(message); // 원본(data.get(id)) 수정 방지용


    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data.values()); //copyOf: map에 저장된 키값만 꺼내서 외부에서 수정못하게 막아줌
    }

    @Override
    public void update(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("메세지가 NULL값입니다.");
        }
        data.put(message.getId(), message); //같은 uuid면 message 값 덮어씀
    }

    @Override
    public void delete(Message message) {
        if (!data.containsKey(message.getId())) {
            throw new IllegalArgumentException("해당 ID를 가진 채널이 존재하지 않습니다.");
        }
        data.remove(message.getId());
    }
}
