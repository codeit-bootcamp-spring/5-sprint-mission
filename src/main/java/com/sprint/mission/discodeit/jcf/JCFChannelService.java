package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> channels = new HashMap<>();

    // 채널 생성
    @Override
    public void create(Channel channel) {
        channels.put(channel.getId(), channel);
    }

    // 삭제되지 않은 채널 조회 (소프트 삭제 적용)
    @Override
    public Channel findById(UUID id) {
        Channel channel = channels.get(id);
        if (channel != null && !channel.isDeleted()) {
            return channel;
        }
        return null;
    }

    // 삭제되지 않은 모든 채널 리스트 반환
    @Override
    public List<Channel> findAll() {
        List<Channel> result = new ArrayList<>();
        for (Channel channel : channels.values()) {
            if (!channel.isDeleted()) {
                result.add(channel);
            }
        }
        return result;
    }

    // 채널 정보 업데이트 (삭제된 채널은 업데이트 불가)
    @Override
    public void update(UUID id, String name, String description) {
        Channel channel = channels.get(id);
        if (channel != null && !channel.isDeleted()) {
            channel.update(name, description);
        }
    }

    // 유저를 채널에 추가 (소프트 삭제된 채널은 제외)
    @Override
    public boolean joinUser(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        return channel != null && channel.addUser(userId);
    }

    // 유저를 채널에서 제거 (소프트 삭제된 채널은 제외)
    @Override
    public boolean leaveUser(UUID id, UUID userId) {
        Channel channel = findById(id);
        return channel != null && channel.removeUser(userId);
    }

    // 메시지를 채널에 추가 (소프트 삭제된 채널은 제외)
    @Override
    public boolean addMessage(UUID channelId, UUID messageId) {
        Channel channel = findById(channelId);
        if (channel == null) return false;
        channel.addMessage(messageId);
        return true;
    }

    // 채널 삭제 (소프트 삭제 적용)
    @Override
    public void delete(UUID id) {
        Channel channel = channels.get(id);
        if (channel != null) {
            channel.delete();
        }
    }

    // 삭제 여부와 무관하게 채널 존재 여부만 확인
    public boolean exists(UUID id) {
        return channels.containsKey(id);
    }
}
