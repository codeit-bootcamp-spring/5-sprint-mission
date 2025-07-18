package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel createChannel(String channelName) {
        if (channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 다시 입력해주세요.");
        }
        Channel channel = new Channel(channelName);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findChannel(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAllChannels() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel updateChannel(UUID id, String channelName) {
        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalArgumentException("없는 채널입니다.");
        }

        if (channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("채널 이름을 입력 해주세요.");
        }
        return  channel.update(channelName);

    }

    @Override
    public Channel deleteChannel(UUID id) {

        if (id == null) {
            throw new IllegalArgumentException("삭제할 채널 ID는 필수입니다.");
        }
        Channel removedChannel = data.remove(id);

        if (removedChannel == null) {
            throw new NoSuchElementException(id + "에 해당하는 채널을 찾을 수 없어 삭제할 수 없습니다.");
        }

        return removedChannel;
    }
}
