package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCAMessageService implements MessageService {

    private final Map<UUID, Message> data;

    private final UserService userService;
    private final ChannelService channelService;

    public JCAMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
        this.data = new LinkedHashMap<>();
    }


    // 메시지 조회
    public List<Message> findAll(){
        return new ArrayList<>(data.values());
    }

    // 메시지 생성
    public Message create(User user, Channel channel, String content) {
        if (userService.findById(user.getId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        if (channelService.findById(channel.getId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

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
