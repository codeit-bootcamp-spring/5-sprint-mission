package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel addChannel(String channelName, UUID ownerUserId);
    Channel getChannelById(UUID channelId);
    List<Channel> getAllChannel();
    Channel updateChannel(UUID channelId, String channelName);
    void deleteChannel(UUID channelId);
    void deleteAllChannel();

    /**
     * 굳이 더 넣는다면
     * addUser : 채널에 유저 추가하기
     * addMessage : 채널에 메시지 추가하기
     *
     * deleteUser: 채널에 유저 삭제하기
     * deleteMessage : 채널에 메시지 삭제하기
     */
}

