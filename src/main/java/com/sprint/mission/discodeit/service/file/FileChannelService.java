package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final FileChannelRepository channelRepository = new FileChannelRepository();

    @Override
    public Channel create(Channel channel) {
        return channelRepository.save(channel);
    }

    @Override
    public Channel findById(UUID id) {
        return channelRepository.findById(id);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID id, String newTitle) {
        Channel existing = channelRepository.findById(id);
        existing.updateTimestamp(); // 내용 수정은 요구사항에 명시되지 않았으므로 생략
        return channelRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        channelRepository.deleteById(id);
    }
}
