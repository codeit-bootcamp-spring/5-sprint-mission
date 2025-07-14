package com.sprint.mission.discodeit.service;

import java.util.UUID;

public interface ChatService {

    boolean joinChannel(UUID userId, UUID channelId);                       // 유저가 지정된 채널에 참가
    boolean leaveChannel(UUID userId, UUID channelId);                      // 유저가 지정된 채널에서 나감
    boolean sendMessage(UUID userId, UUID channelId, String content);       // 유저가 지정된 채널에 메시지를 전송
    void viewChannel(UUID channelId);                                       // 지정된 채널의 정보를 조회하고 메시지 내역 및 참가자 목록을 출력
}
