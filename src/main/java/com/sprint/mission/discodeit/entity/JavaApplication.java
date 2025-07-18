package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.*;

import java.util.*;

public class JavaApplication {
    public static void main(String[] args) {
        Map<UUID, User> userData = new HashMap<>();
        Map<UUID, Channel> channelData = new HashMap<>();
        Map<UUID, Message> messageData = new HashMap<>();

        User user = new User("홍길동", "hong123", "pass123");
        userData.put(user.getId(), user);
        System.out.println("[User 등록] ID: " + user.getId());

        Channel channel = new Channel("일반 채널");
        channelData.put(channel.getId(), channel);
        System.out.println("[Channel 등록] ID: " + channel.getId());

        Message message = new Message("안녕하세요!", channel.getId(), user.getId());
        messageData.put(message.getId(), message);
        System.out.println("[Message 등록] ID: " + message.getId());

        Message readMsg = messageData.get(message.getId());
        System.out.println("[Message 조회] 내용: " + readMsg.getContent());

        message.update("수정된 메시지입니다.");
        System.out.println("[Message 수정 후] 내용: " + message.getContent());

        messageData.remove(message.getId());
        System.out.println("[Message 삭제 완료]");

        System.out.println("[Message 삭제 확인] 결과: " + messageData.get(message.getId()));
    }
}
