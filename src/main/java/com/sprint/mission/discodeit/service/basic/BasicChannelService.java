package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

   private final ChannelRepository channelRepository;

    @Override
    public Channel addChannel(String channelName, UUID ownerUserId) {
        Channel channel = new Channel(channelName, ownerUserId);
        Optional<Channel> addedChannel = channelRepository.save(channel);

        return addedChannel.orElseThrow();
    }

    @Override
    public Channel getChannelById(UUID channelId) {
        Optional<Channel> foundedChannel = channelRepository.findById(channelId);
        return foundedChannel.orElseThrow();
    }

    @Override
    public List<Channel> getAllChannel() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(UUID channelId, String channelName) {
        Channel channel = channelRepository.findById(channelId).orElseThrow();
        channel.updateChannelName(channelName);
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public void deleteChannel(UUID channelId) {
        channelRepository.delete(channelId);
    }

    @Override
    public void deleteAllChannel() {
        channelRepository.deleteAll();
    }

}
