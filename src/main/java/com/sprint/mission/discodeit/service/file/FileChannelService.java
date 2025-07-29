package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private final ChannelRepository channelRepository = new FileChannelRepository();

    @Override
    public Channel createChannel(String channelName) {
        if(channelName==null || channelName.isBlank()){
            throw new IllegalArgumentException("채널 이름을 입력하세요.");
        }
        Channel channel = new Channel(channelName);
        return channelRepository.save(channel);
    }

    @Override
    public Optional<Channel> findChannel(UUID channelId) {
        return channelRepository.findById(channelId);
    }

    @Override
    public List<Channel> findAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(UUID channelId, String channelName) {

        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new IllegalArgumentException("해당 채널이 없습니다."));

        return channelRepository.update(channel.update(channelName));
    }

    @Override
    public Channel deleteChannel(UUID channelId) {
        return channelRepository.delete(channelId);
    }
}
