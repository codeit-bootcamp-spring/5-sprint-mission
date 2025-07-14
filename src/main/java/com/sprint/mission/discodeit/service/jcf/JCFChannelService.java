package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;

public class JCFChannelService implements ChannelService {

    private final List<Channel> data;

    public JCFChannelService() {
        data = new ArrayList<>();
    }

    @Override
    public void addChannel(Channel channel) {
        data.add(channel);
    }

    @Override
    public void updateChannel(Channel channel) {
        int i = data.indexOf(channel);
        data.set(i, channel);
    }

    @Override
    public void deleteChannel(Channel channel) {
        data.remove(channel);
    }

    @Override
    public Channel getChannel(int i) {
        return data.get(i);
    }

    @Override
    public List<Channel> getAllChannels() {
        return data;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JCFChannelService{");
        sb.append("data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
