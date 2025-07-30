package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

   private final ChannelRepository channelRepository;

    public JCFChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel addChannel(String channelName, User ownerUser) {
        if(ownerUser == null){
            throw new IllegalArgumentException("userId가 잘못됨");
        }

        Channel channel = new Channel(channelName, ownerUser);
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
        channelRepository.findById(channelId).ifPresent(channelRepository::delete);
    }

    @Override
    public void deleteAllChannel() {
        channelRepository.deleteAll();
    }

    @Override
    public void addUserToChannel(UUID channelId, User user) {
        Optional<Channel> optionalChannel = channelRepository.findById(channelId);
        Channel channel = optionalChannel.orElseThrow();
        channel.addUser(user);
        channelRepository.save(channel);
    }

    @Override
    public void deleteUserFromChannel(UUID channelId, User user) {
        Optional<Channel> optionalChannel = channelRepository.findById(channelId);
        Channel channel = optionalChannel.orElseThrow();
        channel.removeUser(user);
        channelRepository.save(channel);
    }
}
