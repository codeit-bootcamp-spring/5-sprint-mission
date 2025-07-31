package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data = new HashMap<>();
    private final UserService userService; //메시지에는 유저 아이디
    private final ChannelService channelService; //그리고 채널 아이디가 필요하기 떄문에..

    public JCFMessageService(UserService userService, ChannelService channelService){
        this.userService = userService;
        this.channelService = channelService;
    }

    public Message create(UUID userId, UUID chId, String messageKey, String message){
        if(userService.findById(userId) == null){
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if(channelService.findById(chId) == null){
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        Message ms = new Message(userId, chId, messageKey, message);
        data.put(ms.getId(), ms);
        return ms;
    }

    public Message findMessageKey(String messageKey){
        return data.values().stream()
                .filter(msg -> msg.getMessageKey().equals(messageKey))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 messageKey를 가진 메시지를 찾을 수 없습니다."));
    }

    public List<Message> findAll(){
        return new ArrayList<>(data.values());
    }

    public List<Message> findByUserId(UUID userId){
        return data.values().stream()
                .filter(msg -> msg.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public void update(String messageKey, String newMessage) {
        Message ms = data.values().stream()
                .filter(msg -> msg.getMessageKey().equals(messageKey))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 messageKey의 메시지를 찾을 수 없습니다."));

        ms.updateMessage(newMessage);
    }

    public void delete(String messageKey) {
        UUID targetId = data.values().stream()
                .filter(msg -> msg.getMessageKey().equals(messageKey))
                .map(Message::getId)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("메시지가 존재하지 않습니다."));

        data.remove(targetId);
    }
}
