package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCAMessageService implements MessageService {

    private final Map<UUID, Message> data = new LinkedHashMap<>();

    // 메시지 조회
    public List<Message> findAll(){
        return new ArrayList<>(data.values());
    }

    // 메시지 생성
    public Message create(User user, Channel channel, String content) {
        Message message = new Message(user, channel, content);
        data.put(message.getId(), message);
        return message;
    }

    // 메시지 문자열로 찾기
    public List<Message> findByStr(String message) {
        List<Message> messageList = new ArrayList<>();

        for (Message m : data.values()) {
            if (m.getContent() != null && m.getContent().contains(message)) {
                messageList.add(m);
            }
        }
        return messageList;
    }

    // 메시지 수정
    public Message update(UUID id, String message) {
        Message oldMessage = data.get(id);
        if (oldMessage != null) {
            oldMessage.updateContent(message);
        }
        return oldMessage;
    }

    // 메시지 삭제
    public boolean deleteById(UUID id){
        if(data.containsKey(id)){
            data.remove(id);
            System.out.println("메시지가 삭제되었습니다.");
            return true;
        }
        else{
            System.out.println("삭제실패: 메시지를 찾을 수 없습니다");
            return false;
        }
    }

}
