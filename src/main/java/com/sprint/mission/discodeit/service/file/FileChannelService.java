package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private final ChannelRepository repository;

    //생성자 주입
    public FileChannelService(ChannelRepository repository) {
        this.repository = repository;
    }

    @Override

    public void create(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("채널 정보가 없습니다");
        }
        if (channel.getTitle() == null || channel.getTitle().isEmpty()) {
            throw new IllegalArgumentException("채널 이름을 입력해주세요.");
        }
        repository.save(channel);
    }

    @Override
    public Channel findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Channel> findAll() {
        return repository.findAll();
    }

    @Override
    public void update(Channel channel) {
        repository.update(channel);
    }

    @Override
    public void delete(UUID id) {
        repository.delete(id);
    }
}
