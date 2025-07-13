package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private static final List<Channel> data = new ArrayList<>();
    private static JCFChannelService instance;

    private JCFChannelService() {}

    public static JCFChannelService getInstance() {
        if (instance == null) {
            instance = new JCFChannelService();
        }
        return instance;
    }


    @Override
    public boolean addChannel(Channel channel) {
        if(channel == null){
            return false;
        }

        data.add(channel);
        return true;
    }

    @Override
    public List<Channel> getChannels() {
        return data;
    }

    @Override
    public Channel getChannelById(UUID channelId) {
        return data.stream().filter(c->c.getId().equals(channelId)).findFirst().orElse(null);
    }

    @Override
    public Channel updateChannel(Channel Channel, UUID id) {
        return data.stream().filter(existing->existing.getId().equals(id))
                .findFirst()
                .map(existing->{
                    existing.updateName(Channel.getName());
                    existing.updateType(Channel.getType());
                    existing.updateOwnerUser(Channel.getOwnerUser());
                    existing.updateTopic(Channel.getTopic());
                    existing.updateUpdatedAt(System.currentTimeMillis());

                    return existing;
                })
                .orElse(null);
    }

    @Override
    public Channel deleteChannel(UUID id) {
        return data.stream()
                .filter(existing->existing.getId().equals(id))
                .findFirst()
                .map(existing->{
                    data.remove(existing);
                    return existing;
                })
                .orElse(null);
    }

    @Override
    public void deleteAll() {
        data.clear();
    }
}
