package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChatService;
import com.sprint.mission.discodeit.util.Logger;

import java.util.Date;
import java.util.UUID;

import static com.sprint.mission.discodeit.util.Logger.*;

/**
 * 유저, 채널, 메시지 서비스를 연결해주는 중간 서비스입니다.
 * 채널 참여/퇴장, 메시지 전송, 채널 조회 등 채팅의 핵심 흐름을 담당합니다.
 *
 * 내부적으로 JCF 기반의 유저/채널/메시지 서비스를 의존하며,
 * 각 객체의 유효성을 검사하고 상태를 함께 관리합니다.
 */
public class SimpleChatService implements ChatService {

    private final JCFUserService jcfUserService;
    private final JCFChannelService jcfChannelService;
    private final JCFMessageService jcfMessageService;

    public SimpleChatService(JCFUserService jcfUserService, JCFChannelService jcfChannelService, JCFMessageService jcfMessageService) {
        this.jcfUserService = jcfUserService;
        this.jcfChannelService = jcfChannelService;
        this.jcfMessageService = jcfMessageService;
    }

    // 유저를 채널에 참가시킴
    @Override
    public boolean joinChannel(UUID userId, UUID channelId) {
        log("joinChannel", "유저 ID: " + userId + ", 채널 ID: " + channelId);

        if (userId == null || jcfUserService.findById(userId, false) == null) {
            log("joinChannel", "유저가 존재하지 않음");
            return false;
        }

        if (channelId == null || jcfChannelService.findById(channelId, false) == null) {
            log("joinChannel", "채널이 존재하지 않음");
            return false;
        }

        boolean userJoined = jcfUserService.joinChannel(userId, channelId);
        boolean channelJoined = jcfChannelService.joinUser(channelId, userId);

        log("joinChannel", "유저 참가 결과: " + userJoined + ", 채널 참가 결과: " + channelJoined);
        return userJoined && channelJoined;
    }

    // 채널에서 유저를 퇴장시킴
    @Override
    public boolean leaveChannel(UUID userId, UUID channelId) {
        log("leaveChannel", "유저 ID: " + userId + ", 채널 ID: " + channelId);

        if (userId == null || jcfUserService.findById(userId, false) == null) {
            log("leaveChannel", "유저가 존재하지 않음");
            return false;
        }

        if (channelId == null || jcfChannelService.findById(channelId, false) == null) {
            log("leaveChannel", "채널이 존재하지 않음");
            return false;
        }

        boolean userLeft = jcfUserService.leaveChannel(userId, channelId);
        boolean channelLeft = jcfChannelService.leaveUser(channelId, userId);

        log("leaveChannel", "유저 퇴장 결과: " + userLeft + ", 채널 퇴장 결과: " + channelLeft);
        return userLeft && channelLeft;
    }

    // 유저가 채널에 메시지를 보냄
    @Override
    public boolean sendMessage(UUID userId, UUID channelId, String content) {
        log("sendMessage", "유저 ID: " + userId + ", 채널 ID: " + channelId + ", 내용: " + content);

        if (userId == null || jcfUserService.findById(userId, false) == null) {
            log("sendMessage", "유저가 존재하지 않음");
            return false;
        }

        if (channelId == null || jcfChannelService.findById(channelId, false) == null) {
            log("sendMessage", "채널이 존재하지 않음");
            return false;
        }

        if (!jcfUserService.isUserInChannel(userId, channelId)) {
            log("sendMessage", "유저가 해당 채널에 참여하고 있지 않음");
            return false;
        }

        Message message = new Message(userId, channelId, content);
        jcfMessageService.create(message);
        jcfChannelService.addMessage(channelId, message.getId());
        jcfUserService.addMessage(userId, message.getId());

        log("sendMessage", "메시지 전송 완료 → ID: " + message.getId());
        return true;
    }

    // 채널 정보를 출력
    @Override
    public void viewChannel(UUID channelId) {
        Channel channel = jcfChannelService.findById(channelId, false);

        logSection("채널 정보 조회");

        if (channel == null) {
            if (!jcfChannelService.exists(channelId)) {
                log("viewChannel", "채널이 존재하지 않음");
            } else {
                log("viewChannel", "알 수 없는 채널 (삭제됨)");
            }
            return;
        }

        log("NAME",  channel.getName());
        log("DESCRIPTION",  channel.getDescription());
        log("CREATED AT",  new Date(channel.getCreatedAt()).toString());
        log("TOTAL MEMBER", String.valueOf(channel.getCount()));

        logSection("참가자 목록");
        for (UUID userId : channel.getUserIds()) {
            var user = jcfUserService.findById(userId, false);
            if (user != null) {
                log("MEMBER", user.getName() + " (" + user.getAge() + "세)");
            } else if (jcfUserService.exists(userId)) {
                log("MEMBER", "알 수 없음 (삭제된 유저)");
            } else {
                log("MEMBER", "존재하지 않는 유저");
            }
        }

        logSection("채팅 내역");
        for (UUID messageId : channel.getMessageIds()) {
            if (!jcfMessageService.exists(messageId)) {
                log("SYSTEM", "존재하지 않는 메시지입니다.");
                continue;
            }

            Message message = jcfMessageService.findById(messageId);
            if (message == null) {
                log("SYSTEM", "삭제된 메시지입니다.");
                continue;
            }

            var sender = jcfUserService.findById(message.getUserId(), false);
            String senderName = (sender != null) ? sender.getName() : "알 수 없음";
            log(senderName, message.getContent());
        }
    }
}