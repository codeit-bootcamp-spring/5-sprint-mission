package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> channelList;

    public JCFChannelService() {
        channelList = new ArrayList<>();
    }

    // 채널 추가
    @Override
    public Channel insert(String name, UUID ownerId) {
        if (name == null || ownerId == null || name.isBlank()) {
            return null;
        }
        Channel channel = new Channel(name, ownerId);
        channelList.add(channel);
        return channel;
    }

    // 채널 조회
    @Override
    public Channel selectOne(UUID channelId) {
        for (Channel channel : channelList) {
            if (channel.getId().equals(channelId)) {
                return channel;
            }
        }
        return null;
    }

    // 채널 전체 조회
    @Override
    public List<Channel> selectAll() {
        return channelList;
    }

    // 채널 수정
    @Override
    public Channel update(UUID channelId, String name, UUID ownerId) {
        for (Channel channel : channelList) {
            if (channel.getId().equals(channelId)) {
                channel.update(name, ownerId);
                return channel;
            }
        }
        return null;
    }

    // 채널 삭제
    @Override
    public boolean delete(UUID channelId) {
        for (int i = 0; i < channelList.size(); i++) {
            Channel channel = channelList.get(i);
            if (channel.getId().equals(channelId)) {
                channelList.remove(i);
                return true;
            }
        }
        return false;
    }
}
