package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChatService;

import java.util.Date;
import java.util.UUID;

public class SimpleChatService implements ChatService {

    private final JCFUserService jcfUserService;
    private final JCFChannelService jcfChannelService;
    private final JFCMessageService jcfMessageService;

    public SimpleChatService(JCFUserService jcfUserService, JCFChannelService jcfChannelService, JFCMessageService jcfMessageService) {
        this.jcfUserService = jcfUserService;
        this.jcfChannelService = jcfChannelService;
        this.jcfMessageService = jcfMessageService;
    }

    // 유저가 채널에 참가
    @Override
    public boolean joinChannel(UUID userId, UUID channelId) {
        System.out.println("[joinChannel] 유저 ID: " + userId + ", 채널 ID: " + channelId);

        if (jcfUserService.findById(userId) == null) {
            System.out.println("유저가 존재하지 않습니다.");
            return false;
        }

        if (jcfChannelService.findById(channelId) == null) {
            System.out.println("채널이 존재하지 않습니다.");
            return false;
        }

        boolean userJoined = jcfUserService.joinChannel(userId, channelId);
        boolean channelJoined = jcfChannelService.joinUser(channelId, userId);

        System.out.println("유저 참가 결과: " + userJoined + ", 채널 참가 결과: " + channelJoined);
        System.out.println();

        return userJoined && channelJoined;
    }

    // 유저가 채널을 떠남
    @Override
    public boolean leaveChannel(UUID userId, UUID channelId) {
        System.out.println("[leaveChannel] 유저 ID: " + userId + ", 채널 ID: " + channelId);

        if (jcfUserService.findById(userId) == null) {
            System.out.println("유저가 존재하지 않습니다.");
            return false;
        }

        if (jcfChannelService.findById(channelId) == null) {
            System.out.println("채널이 존재하지 않습니다.");
            return false;
        }

        boolean userLeft = jcfUserService.leaveChannel(userId, channelId);
        boolean channelLeft = jcfChannelService.leaveUser(channelId, userId);

        System.out.println("유저 퇴장 결과: " + userLeft + ", 채널 퇴장 결과: " + channelLeft);
        System.out.println();

        return userLeft && channelLeft;
    }

    // 유저가 채널에 메시지를 전송
    @Override
    public boolean sendMessage(UUID userId, UUID channelId, String content) {
        System.out.println("[sendMessage] 유저 ID: " + userId + ", 채널 ID: " + channelId + ", 내용: " + content);

        if (jcfUserService.findById(userId) == null) {
            System.out.println("유저가 존재하지 않음");
            return false;
        }

        if (jcfChannelService.findById(channelId) == null) {
            System.out.println("채널이 존재하지 않음");
            return false;
        }

        if (!jcfUserService.isUserInChannel(userId, channelId)) {
            System.out.println("유저가 해당 채널에 참여하고 있지 않음");
            return false;
        }

        Message message = new Message(userId, channelId, content);
        jcfMessageService.create(message);
        jcfChannelService.addMessage(channelId, message.getId());
        jcfUserService.addMessage(userId, message.getId());

        System.out.println("메시지 전송 완료: " + message);
        System.out.println();

        return true;
    }

    // 채널 정보를 출력
    @Override
    public void viewChannel(UUID channelId) {
        Channel channel = jcfChannelService.findById(channelId);

        if (channel == null) {
            if (!jcfChannelService.exists(channelId)) {
                System.out.println("채널이 존재하지 않습니다.");
            } else {
                System.out.println("알 수 없는 채널 (삭제됨)");
            }
            return;
        }

        System.out.println("채널 정보");
        System.out.println("제목: " + channel.getName());
        System.out.println("설명: " + channel.getDescription());
        System.out.println("생성일: " + new Date(channel.getCreatedAt()));
        System.out.println("참가 인원 수: " + channel.getCount());
        System.out.println();

        System.out.println("참가자 목록");
        for (UUID userId : channel.getUserIds()) {
            var user = jcfUserService.findById(userId);
            if (user != null) {
                System.out.println("- " + user.getName() + " (" + user.getAge() + "세)");
            } else if (jcfUserService.exists(userId)) {
                System.out.println("- 알 수 없음 (삭제된 유저)");
            } else {
                System.out.println("- 존재하지 않는 유저");
            }
        }

        System.out.println();

        System.out.println("채팅 내역");
        for (UUID messageId : channel.getMessageIds()) {
            if (!jcfMessageService.exists(messageId)) {
                System.out.println("[시스템] 존재하지 않는 메시지입니다.");
                continue;
            }

            Message message = jcfMessageService.findById(messageId);
            if (message == null) {
                System.out.println("[시스템] 삭제된 메시지입니다.");
                continue;
            }

            var sender = jcfUserService.findById(message.getUserId());
            String senderName = (sender != null) ? sender.getName() : "알 수 없음";

            System.out.println("[" + senderName + "] " + message.getContent());
        }

        System.out.println();
    }
}

